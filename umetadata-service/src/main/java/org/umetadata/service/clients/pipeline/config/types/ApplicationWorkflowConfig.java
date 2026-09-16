package org.umetadata.service.clients.pipeline.config.types;

import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.metadataIngestion.ApplicationPipeline;
import org.umetadata.schema.metadataIngestion.UMetadataAppConfig;
import org.umetadata.schema.utils.JsonUtils;

public class ApplicationWorkflowConfig {
  public UMetadataAppConfig buildOMApplicationConfig(IngestionPipeline ingestionPipeline) {

    ApplicationPipeline externalApplicationConfig =
        JsonUtils.convertValue(
            ingestionPipeline.getSourceConfig().getConfig(), ApplicationPipeline.class);

    return new UMetadataAppConfig()
        .withSourcePythonClass(externalApplicationConfig.getSourcePythonClass())
        .withAppConfig(externalApplicationConfig.getAppConfig())
        .withAppPrivateConfig(externalApplicationConfig.getAppPrivateConfig());
  }
}
