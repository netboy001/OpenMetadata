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

package org.umetadata.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.server.DefaultServerFactory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.umetadata.DefaultOperationalConfigProvider;
import org.umetadata.schema.api.configuration.dataQuality.DataQualityConfiguration;
import org.umetadata.schema.api.configuration.events.EventHandlerConfiguration;
import org.umetadata.schema.api.configuration.pipelineServiceClient.PipelineServiceClientConfiguration;
import org.umetadata.schema.api.configuration.rdf.RdfConfiguration;
import org.umetadata.schema.api.fernet.FernetConfiguration;
import org.umetadata.schema.api.security.AuthenticationConfiguration;
import org.umetadata.schema.api.security.AuthorizerConfiguration;
import org.umetadata.schema.api.security.OpsConfig;
import org.umetadata.schema.api.security.jwt.JWTTokenConfiguration;
import org.umetadata.schema.configuration.AiPlatformConfiguration;
import org.umetadata.schema.configuration.LimitsConfiguration;
import org.umetadata.schema.security.scim.ScimConfiguration;
import org.umetadata.schema.security.secrets.SecretsManagerConfiguration;
import org.umetadata.schema.service.configuration.elasticsearch.ElasticSearchConfiguration;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.config.BulkOperationConfiguration;
import org.umetadata.service.config.CacheConfiguration;
import org.umetadata.service.config.OMWebConfiguration;
import org.umetadata.service.config.ObjectStorageConfiguration;
import org.umetadata.service.jdbi3.HikariCPDataSourceFactory;
import org.umetadata.service.migration.MigrationConfiguration;
import org.umetadata.service.monitoring.EventMonitorConfiguration;
import org.umetadata.service.swagger.SwaggerBundleConfiguration;

@Getter
@Setter
public class UMetadataApplicationConfig extends Configuration {

  @Getter @JsonProperty private String basePath;

  @Getter
  @JsonProperty("assets")
  private Map<String, String> assets;

  @JsonProperty("database")
  @NotNull
  @Valid
  private HikariCPDataSourceFactory dataSourceFactory;

  @JsonProperty("swagger")
  private SwaggerBundleConfiguration swaggerBundleConfig;

  @JsonProperty("authorizerConfiguration")
  private AuthorizerConfiguration authorizerConfiguration;

  @JsonProperty("authenticationConfiguration")
  private AuthenticationConfiguration authenticationConfiguration;

  @JsonProperty("jwtTokenConfiguration")
  private JWTTokenConfiguration jwtTokenConfiguration;

  @JsonProperty("elasticsearch")
  private ElasticSearchConfiguration elasticSearchConfiguration;

  @JsonProperty("eventHandlerConfiguration")
  private EventHandlerConfiguration eventHandlerConfiguration;

  @JsonProperty("pipelineServiceClientConfiguration")
  private PipelineServiceClientConfiguration pipelineServiceClientConfiguration;

  @JsonProperty("operationalConfig")
  private OpsConfig opsConfig;

  private DefaultOperationalConfigProvider operationalApplicationConfigProvider;

  private static final String CERTIFICATE_PATH = "certificatePath";

  public PipelineServiceClientConfiguration getPipelineServiceClientConfiguration() {
    if (pipelineServiceClientConfiguration != null) {
      Map<String, String> temporarySSLConfig =
          JsonUtils.readOrConvertValue(
              pipelineServiceClientConfiguration.getSslConfig(), Map.class);
      if (temporarySSLConfig != null && temporarySSLConfig.containsKey(CERTIFICATE_PATH)) {
        temporarySSLConfig.put("caCertificate", temporarySSLConfig.get(CERTIFICATE_PATH));
        temporarySSLConfig.remove(CERTIFICATE_PATH);
      }
      pipelineServiceClientConfiguration.setSslConfig(temporarySSLConfig);
    }
    return pipelineServiceClientConfiguration;
  }

  public DefaultOperationalConfigProvider getOperationalApplicationConfigProvider() {
    if (operationalApplicationConfigProvider == null) {
      operationalApplicationConfigProvider = new DefaultOperationalConfigProvider(getOpsConfig());
    }
    return operationalApplicationConfigProvider;
  }

  public OpsConfig getOpsConfig() {
    if (opsConfig == null) {
      opsConfig = new OpsConfig().withEnable(false);
    }
    return opsConfig;
  }

  @JsonProperty("migrationConfiguration")
  @NotNull
  private MigrationConfiguration migrationConfiguration;

  @JsonProperty("fernetConfiguration")
  private FernetConfiguration fernetConfiguration;

  @JsonProperty("secretsManagerConfiguration")
  private SecretsManagerConfiguration secretsManagerConfiguration;

  @JsonProperty("eventMonitoringConfiguration")
  private EventMonitorConfiguration eventMonitorConfiguration;

  @JsonProperty("clusterName")
  private String clusterName;

  @Valid
  @NotNull
  @JsonProperty("web")
  private OMWebConfiguration webConfiguration = new OMWebConfiguration();

  @JsonProperty("dataQualityConfiguration")
  private DataQualityConfiguration dataQualityConfiguration;

  @JsonProperty("limits")
  private LimitsConfiguration limitsConfiguration;

  @JsonProperty("objectStorage")
  @Valid
  private ObjectStorageConfiguration objectStorage;

  @JsonProperty("scimConfiguration")
  private ScimConfiguration scimConfiguration;

  @JsonProperty("aiPlatformConfiguration")
  private AiPlatformConfiguration aiPlatformConfiguration;

  @JsonProperty("mcpConfiguration")
  private org.umetadata.schema.api.configuration.MCPConfiguration mcpConfiguration;

  @JsonProperty("rdf")
  private RdfConfiguration rdfConfiguration = new RdfConfiguration();

  @JsonProperty("cache")
  private org.umetadata.service.cache.CacheConfig cacheConfig;

  public org.umetadata.service.cache.CacheConfig getCacheConfig() {
    if (cacheConfig == null) {
      cacheConfig = new org.umetadata.service.cache.CacheConfig();
    }
    return cacheConfig;
  }

  @JsonProperty("bulkOperation")
  @Valid
  private BulkOperationConfiguration bulkOperationConfiguration;

  public BulkOperationConfiguration getBulkOperationConfiguration() {
    if (bulkOperationConfiguration == null) {
      bulkOperationConfiguration = new BulkOperationConfiguration();
    }
    return bulkOperationConfiguration;
  }

  @JsonProperty("cacheMemory")
  @Valid
  private CacheConfiguration cacheMemoryConfiguration = new CacheConfiguration();

  public CacheConfiguration getCacheMemoryConfiguration() {
    if (cacheMemoryConfiguration == null) {
      cacheMemoryConfiguration = new CacheConfiguration();
    }
    return cacheMemoryConfiguration;
  }

  public String getApiRootPath() {
    if (!(getServerFactory() instanceof DefaultServerFactory serverFactory)) {
      return "";
    }
    return serverFactory.getJerseyRootPath().map(path -> path.replaceFirst("\\*$", "")).orElse("");
  }

  @Override
  public String toString() {
    return "catalogConfig{"
        + ", dataSourceFactory="
        + dataSourceFactory
        + ", swaggerBundleConfig="
        + swaggerBundleConfig
        + ", authorizerConfiguration="
        + authorizerConfiguration
        + '}';
  }
}
