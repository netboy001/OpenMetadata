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

package org.umetadata.service.clients.pipeline.config;

import java.util.UUID;
import org.umetadata.schema.api.services.CreateDatabaseService;
import org.umetadata.schema.api.services.CreateMetadataService;
import org.umetadata.schema.api.services.DatabaseConnection;
import org.umetadata.schema.entity.app.external.CollateAIAppConfig;
import org.umetadata.schema.entity.services.DatabaseService;
import org.umetadata.schema.entity.services.MetadataConnection;
import org.umetadata.schema.entity.services.MetadataService;
import org.umetadata.schema.entity.services.ingestionPipelines.AirflowConfig;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineType;
import org.umetadata.schema.metadataIngestion.ApplicationPipeline;
import org.umetadata.schema.metadataIngestion.DatabaseServiceMetadataPipeline;
import org.umetadata.schema.metadataIngestion.LogLevels;
import org.umetadata.schema.metadataIngestion.SourceConfig;
import org.umetadata.schema.security.client.UMetadataJWTClientConfig;
import org.umetadata.schema.security.secrets.SecretsManagerClientLoader;
import org.umetadata.schema.security.secrets.SecretsManagerProvider;
import org.umetadata.schema.security.ssl.VerifySSL;
import org.umetadata.schema.services.connections.database.MysqlConnection;
import org.umetadata.schema.services.connections.metadata.AuthProvider;
import org.umetadata.schema.services.connections.metadata.ComponentConfig;
import org.umetadata.schema.services.connections.metadata.ElasticsSearch;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.schema.type.EntityReference;

public abstract class WorkflowConfigTest {

  public static final String MOCK_SERVICE_NAME = "mysqlDB";
  public static final DatabaseService MOCK_SERVICE =
      new DatabaseService()
          .withId(UUID.randomUUID())
          .withServiceType(CreateDatabaseService.DatabaseServiceType.Mysql)
          .withName(MOCK_SERVICE_NAME)
          .withConnection(
              new DatabaseConnection()
                  .withConfig(
                      new MysqlConnection()
                          .withHostPort("mysql.example.com:3306")
                          .withUsername("mysql_user")
                          .withDatabaseName("testdb")));

  public static final String OM_SERVICE_NAME = "UMetadata";
  public static final MetadataService MOCK_OM_SERVICE =
      new MetadataService()
          .withId(UUID.randomUUID())
          .withServiceType(CreateMetadataService.MetadataServiceType.UMetadataServer)
          .withName(OM_SERVICE_NAME)
          .withConnection(
              new MetadataConnection()
                  .withConfig(
                      new UMetadataConnection()
                          .withHostPort("http://umetadata-server:8585/api")
                          .withElasticsSearch(
                              new ElasticsSearch().withConfig(new ComponentConfig()))));

  IngestionPipeline newMetadataIngestionPipeline() {
    return new IngestionPipeline()
        .withName("testPipeline")
        .withFullyQualifiedName(MOCK_SERVICE_NAME + ".testPipeline")
        .withPipelineType(PipelineType.METADATA)
        .withLoggerLevel(LogLevels.DEBUG)
        .withService(
            new EntityReference()
                .withId(UUID.randomUUID())
                .withType("databaseService")
                .withName(MOCK_SERVICE_NAME))
        .withSourceConfig(new SourceConfig().withConfig(new DatabaseServiceMetadataPipeline()))
        .withAirflowConfig(new AirflowConfig().withRetries(1))
        .withUMetadataServerConnection(
            new UMetadataConnection()
                .withAuthProvider(AuthProvider.UMETADATA)
                .withHostPort("http://umetadata-server:8585/api")
                .withSecurityConfig(new UMetadataJWTClientConfig().withJwtToken("token"))
                .withClusterName("clusterName")
                .withVerifySSL(VerifySSL.NO_SSL)
                .withSecretsManagerLoader(SecretsManagerClientLoader.NOOP)
                .withSecretsManagerProvider(SecretsManagerProvider.DB));
  }

  IngestionPipeline newApplicationPipeline() {
    return new IngestionPipeline()
        .withName("appPipeline")
        .withFullyQualifiedName(OM_SERVICE_NAME + ".appPipeline")
        .withPipelineType(PipelineType.APPLICATION)
        .withLoggerLevel(LogLevels.DEBUG)
        .withService(
            new EntityReference()
                .withId(UUID.randomUUID())
                .withType("metadataService")
                .withName(OM_SERVICE_NAME))
        .withSourceConfig(
            new SourceConfig()
                .withConfig(
                    new ApplicationPipeline()
                        .withType(ApplicationPipeline.ApplicationConfigType.APPLICATION)
                        .withSourcePythonClass("metadata.ingestion.path")
                        .withAppConfig(new CollateAIAppConfig())))
        .withUMetadataServerConnection(
            new UMetadataConnection()
                .withAuthProvider(AuthProvider.UMETADATA)
                .withHostPort("http://umetadata-server:8585/api")
                .withSecurityConfig(new UMetadataJWTClientConfig().withJwtToken("token"))
                .withClusterName("clusterName")
                .withVerifySSL(VerifySSL.NO_SSL)
                .withSecretsManagerLoader(SecretsManagerClientLoader.NOOP)
                .withSecretsManagerProvider(SecretsManagerProvider.DB));
  }
}
