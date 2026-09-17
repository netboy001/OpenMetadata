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

import java.util.Map;
import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineType;
import org.umetadata.schema.metadataIngestion.LogLevels;
import org.umetadata.schema.metadataIngestion.UMetadataAppConfig;
import org.umetadata.schema.metadataIngestion.UMetadataWorkflowConfig;
import org.umetadata.schema.metadataIngestion.Sink;
import org.umetadata.schema.metadataIngestion.Source;
import org.umetadata.schema.metadataIngestion.WorkflowConfig;
import org.umetadata.schema.services.connections.metadata.ComponentConfig;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.clients.pipeline.config.types.ApplicationWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.AutoClassificationWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.DBTWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.LineageWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.MetadataWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.ProfilerWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.TestSuiteWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.UsageWorkflowConfig;
import org.umetadata.service.clients.pipeline.config.types.WorkflowBuildException;
import org.umetadata.service.clients.pipeline.config.types.WorkflowConfigTypeStrategy;

// Create Kubernetes Job configurations based on Ingestion Pipeline configurations
public class WorkflowConfigBuilder {

  public static UMetadataWorkflowConfig buildOMWorkflowConfig(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service)
      throws WorkflowBuildException {

    WorkflowConfigTypeStrategy workflowStrategy;

    switch (ingestionPipeline.getPipelineType()) {
      case METADATA:
        workflowStrategy = new MetadataWorkflowConfig();
        break;
      case USAGE:
        workflowStrategy = new UsageWorkflowConfig();
        break;
      case LINEAGE:
        workflowStrategy = new LineageWorkflowConfig();
        break;
      case PROFILER:
        workflowStrategy = new ProfilerWorkflowConfig();
        break;
      case AUTO_CLASSIFICATION:
        workflowStrategy = new AutoClassificationWorkflowConfig();
        break;
      case TEST_SUITE:
        workflowStrategy = new TestSuiteWorkflowConfig();
        break;
      case DBT:
        workflowStrategy = new DBTWorkflowConfig();
        break;
      default:
        throw new IllegalArgumentException(
            "Not implemented pipeline type: " + ingestionPipeline.getPipelineType());
    }

    UMetadataWorkflowConfig config =
        workflowStrategy.buildOMWorkflowConfig(ingestionPipeline, service);

    LogLevels ingestionLevel =
        ingestionPipeline.getLoggerLevel() != null
            ? ingestionPipeline.getLoggerLevel()
            : LogLevels.INFO;
    // All workflows use the same WorkflowConfig and need the Pipeline FQN
    config.setWorkflowConfig(
        buildDefaultWorkflowConfig(ingestionPipeline).withLoggerLevel(ingestionLevel));
    config.setIngestionPipelineFQN(ingestionPipeline.getFullyQualifiedName());
    config.setEnableStreamableLogs(ingestionPipeline.getEnableStreamableLogs());
    if (service != null && service.getIngestionRunner() != null) {
      config.setIngestionRunnerName(service.getIngestionRunner().getName());
    }
    return config;
  }

  public static UMetadataAppConfig buildOMApplicationConfig(
      IngestionPipeline ingestionPipeline, Map<String, Object> configOverride) {
    ApplicationWorkflowConfig workflowStrategy = new ApplicationWorkflowConfig();

    UMetadataAppConfig config = workflowStrategy.buildOMApplicationConfig(ingestionPipeline);
    if (configOverride != null) {
      Map<String, Object> configMap = JsonUtils.getMap(config.getAppConfig());
      configMap.putAll(configOverride);
      config.setAppConfig(configMap);
    }

    LogLevels ingestionLevel =
        ingestionPipeline.getLoggerLevel() != null
            ? ingestionPipeline.getLoggerLevel()
            : LogLevels.INFO;

    // All workflows use the same WorkflowConfig and need the Pipeline FQN
    config.setWorkflowConfig(
        buildDefaultWorkflowConfig(ingestionPipeline).withLoggerLevel(ingestionLevel));
    config.setIngestionPipelineFQN(ingestionPipeline.getFullyQualifiedName());
    config.setEnableStreamableLogs(ingestionPipeline.getEnableStreamableLogs());
    if (ingestionPipeline.getIngestionRunner() != null) {
      config.setIngestionRunnerName(ingestionPipeline.getIngestionRunner().getName());
    }
    return config;
  }

  public static String buildIngestionStringYaml(
      IngestionPipeline ingestionPipeline,
      ServiceEntityInterface service,
      Map<String, Object> config)
      throws WorkflowBuildException {

    // Check if we are deploying an Application or a Workflow
    if (PipelineType.APPLICATION.equals(ingestionPipeline.getPipelineType())) {
      UMetadataAppConfig appConfig = buildOMApplicationConfig(ingestionPipeline, config);
      return YAMLUtils.stringifiedOMAppConfig(appConfig);
    }

    UMetadataWorkflowConfig workflowConfig = buildOMWorkflowConfig(ingestionPipeline, service);
    return YAMLUtils.stringifiedOMWorkflowConfig(workflowConfig);
  }

  public static Sink buildDefaultSink() {
    Sink sink = new Sink();
    sink.setType("metadata-rest");
    sink.setConfig(new ComponentConfig());
    return sink;
  }

  public static WorkflowConfig buildDefaultWorkflowConfig(IngestionPipeline ingestionPipeline) {
    WorkflowConfig workflowConfig = new WorkflowConfig();
    workflowConfig.setLoggerLevel(
        ingestionPipeline.getLoggerLevel() != null
            ? ingestionPipeline.getLoggerLevel()
            : LogLevels.INFO);

    // Validate UMetadataServerConnection is properly configured
    if (ingestionPipeline.getuMetadataServerConnection() == null) {
      throw new IllegalArgumentException("UMetadata server connection is required but not set");
    }
    if (ingestionPipeline.getuMetadataServerConnection().getSecurityConfig() == null) {
      throw new IllegalArgumentException(
          "UMetadata server connection securityConfig is required but not set");
    }

    workflowConfig.setuMetadataServerConfig(ingestionPipeline.getuMetadataServerConnection());
    return workflowConfig;
  }

  public static Source buildDefaultSource(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service) {
    Source source = new Source();
    source.setServiceName(ingestionPipeline.getService().getName());
    source.setSourceConfig(ingestionPipeline.getSourceConfig());
    source.setType(service.getServiceType().toString().toLowerCase());

    return source;
  }
}
