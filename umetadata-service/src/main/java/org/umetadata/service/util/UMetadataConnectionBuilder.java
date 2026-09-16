/*
 *  Copyright 2021 Collate
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.umetadata.service.util;

import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.api.configuration.pipelineServiceClient.PipelineServiceClientConfiguration;
import org.umetadata.schema.auth.JWTAuthMechanism;
import org.umetadata.schema.auth.SSOAuthMechanism;
import org.umetadata.schema.entity.Bot;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.teams.AuthenticationMechanism;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.schema.security.client.UMetadataJWTClientConfig;
import org.umetadata.schema.security.secrets.SecretsManagerClientLoader;
import org.umetadata.schema.security.secrets.SecretsManagerProvider;
import org.umetadata.schema.security.ssl.ValidateSSLClientConfig;
import org.umetadata.schema.security.ssl.VerifySSL;
import org.umetadata.schema.services.connections.metadata.AuthProvider;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.exception.EntityNotFoundException;
import org.umetadata.service.jdbi3.BotRepository;
import org.umetadata.service.jdbi3.IngestionPipelineRepository;
import org.umetadata.service.jdbi3.UserRepository;
import org.umetadata.service.secrets.SecretsManager;
import org.umetadata.service.secrets.SecretsManagerFactory;
import org.umetadata.service.util.EntityUtil.Fields;

@Slf4j
public class UMetadataConnectionBuilder {

  AuthProvider authProvider;
  UMetadataJWTClientConfig securityConfig;
  private VerifySSL verifySSL;
  private String uMetadataURL;
  private String clusterName;
  private SecretsManagerProvider secretsManagerProvider;
  private SecretsManagerClientLoader secretsManagerLoader;
  private Object uMetadataSSLConfig;
  BotRepository botRepository;
  UserRepository userRepository;
  SecretsManager secretsManager;

  public UMetadataConnectionBuilder(
      UMetadataApplicationConfig uMetadataApplicationConfig) {
    initializeUMetadataConnectionBuilder(uMetadataApplicationConfig);
    initializeBotUser(Entity.INGESTION_BOT_NAME);
  }

  public UMetadataConnectionBuilder(
      UMetadataApplicationConfig uMetadataApplicationConfig, String botName) {
    initializeUMetadataConnectionBuilder(uMetadataApplicationConfig);
    initializeBotUser(botName);
  }

  public UMetadataConnectionBuilder(
      UMetadataApplicationConfig uMetadataApplicationConfig,
      IngestionPipeline ingestionPipeline) {
    initializeUMetadataConnectionBuilder(uMetadataApplicationConfig);
    // Try to load the pipeline bot or default to using the ingestion bot
    try {
      initializeBotUser(getBotFromPipeline(ingestionPipeline));
    } catch (Exception e) {
      LOG.warn(
          String.format(
              "Could not initialize bot for pipeline [%s] due to [%s]",
              ingestionPipeline.getPipelineType(), e));
      initializeBotUser(Entity.INGESTION_BOT_NAME);
    }
  }

  private String getBotFromPipeline(IngestionPipeline ingestionPipeline) {
    String botName;
    switch (ingestionPipeline.getPipelineType()) {
      case METADATA, DBT -> botName = Entity.INGESTION_BOT_NAME;
      case AUTO_CLASSIFICATION -> botName = "autoClassification-bot";
      case APPLICATION -> {
        String type = IngestionPipelineRepository.getPipelineWorkflowType(ingestionPipeline);
        botName = String.format("%sApplicationBot", type);
      }
      default -> botName =
          String.format("%s-bot", ingestionPipeline.getPipelineType().toString().toLowerCase());
    }
    return botName;
  }

  private void initializeUMetadataConnectionBuilder(
      UMetadataApplicationConfig uMetadataApplicationConfig) {
    botRepository = (BotRepository) Entity.getEntityRepository(Entity.BOT);
    userRepository = (UserRepository) Entity.getEntityRepository(Entity.USER);

    PipelineServiceClientConfiguration pipelineServiceClientConfiguration =
        uMetadataApplicationConfig.getPipelineServiceClientConfiguration();
    uMetadataURL = pipelineServiceClientConfiguration.getMetadataApiEndpoint();
    verifySSL = pipelineServiceClientConfiguration.getVerifySSL();

    /*
     How this information flows:
     - The OM Server has SSL configured
     - We need to provide a way to tell the pipelineServiceClient to use / not use it when connecting
       to the server.

     Then, we pick up this information from the pipelineServiceClient configuration and will pass it
     inside the UMetadataServerConnection property of the IngestionPipeline.

     Based on that, the Ingestion Framework will instantiate the client. This means,
     that the SSL configs we add here are to go from pipelineServiceClient -> UMetadata Server.
    */
    uMetadataSSLConfig =
        getOMSSLConfigFromPipelineServiceClient(
            pipelineServiceClientConfiguration.getVerifySSL(),
            pipelineServiceClientConfiguration.getSslConfig());

    clusterName = uMetadataApplicationConfig.getClusterName();
    secretsManagerLoader = pipelineServiceClientConfiguration.getSecretsManagerLoader();
    secretsManager = SecretsManagerFactory.getSecretsManager();
    secretsManagerProvider = secretsManager.getSecretsManagerProvider();
  }

  private void initializeBotUser(String botName) {
    User botUser = retrieveBotUser(botName);
    securityConfig = extractSecurityConfig(botUser);
    authProvider = extractAuthProvider(botUser);
  }

  private AuthProvider extractAuthProvider(User botUser) {
    AuthenticationMechanism.AuthType authType = botUser.getAuthenticationMechanism().getAuthType();
    return switch (authType) {
      case SSO -> AuthProvider.fromValue(
          JsonUtils.convertValue(
                  botUser.getAuthenticationMechanism().getConfig(), SSOAuthMechanism.class)
              .getSsoServiceType()
              .value());
      case JWT -> AuthProvider.UMETADATA;
      default -> throw new IllegalArgumentException(
          String.format("Not supported authentication mechanism type: [%s]", authType.value()));
    };
  }

  private UMetadataJWTClientConfig extractSecurityConfig(User botUser) {
    AuthenticationMechanism authMechanism = botUser.getAuthenticationMechanism();
    if (Objects.requireNonNull(botUser.getAuthenticationMechanism().getAuthType())
        == AuthenticationMechanism.AuthType.JWT) {
      JWTAuthMechanism jwtAuthMechanism =
          JsonUtils.convertValue(authMechanism.getConfig(), JWTAuthMechanism.class);
      secretsManager.decryptJWTAuthMechanism(jwtAuthMechanism);
      return new UMetadataJWTClientConfig().withJwtToken(jwtAuthMechanism.getJWTToken());
    }
    throw new IllegalArgumentException(
        String.format(
            "Not supported authentication mechanism type: [%s]",
            authMechanism.getAuthType().value()));
  }

  public UMetadataConnection build() {
    return new UMetadataConnection()
        .withAuthProvider(authProvider)
        .withHostPort(uMetadataURL)
        .withSecurityConfig(securityConfig)
        .withVerifySSL(verifySSL)
        .withClusterName(clusterName)
        // What is the SM configuration, i.e., tool used to manage secrets: AWS SM, Parameter
        // Store,...
        .withSecretsManagerProvider(secretsManagerProvider)
        // How the Ingestion Framework will know how to load the SM creds in the client side, e.g.,
        // airflow.cfg
        .withSecretsManagerLoader(secretsManagerLoader)
        /*
        This is not about the pipeline service client SSL, but the OM server SSL.
        The Ingestion Framework will use this value to load the certificates when connecting to the server.
        */
        .withSslConfig(uMetadataSSLConfig);
  }

  private User retrieveBotUser(String botName) {
    User botUser = retrieveIngestionBotUser(botName);
    if (botUser == null) {
      throw new IllegalArgumentException(
          String.format("Please, verify that the bot [%s] is present.", botName));
    }
    return botUser;
  }

  private User retrieveIngestionBotUser(String botName) {
    try {
      Bot bot = botRepository.getByName(null, botName, Fields.EMPTY_FIELDS);
      if (bot.getBotUser() == null) {
        return null;
      }
      User user =
          userRepository.getByName(
              null,
              bot.getBotUser().getFullyQualifiedName(),
              new EntityUtil.Fields(Set.of("authenticationMechanism")));
      if (user.getAuthenticationMechanism() != null) {
        user.getAuthenticationMechanism().setConfig(user.getAuthenticationMechanism().getConfig());
      }
      return user;
    } catch (EntityNotFoundException ex) {
      LOG.debug((String.format("User for bot [%s]", botName)) + " [{}] not found.", botName);
      return null;
    }
  }

  protected Object getOMSSLConfigFromPipelineServiceClient(VerifySSL verifySSL, Object sslConfig) {
    return switch (verifySSL) {
      case NO_SSL, IGNORE -> null;
      case VALIDATE -> JsonUtils.convertValue(sslConfig, ValidateSSLClientConfig.class);
    };
  }
}
