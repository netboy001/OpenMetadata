package org.umetadata.service.clients.pipeline.config.types;

import static org.umetadata.service.clients.pipeline.config.WorkflowConfigBuilder.buildDefaultSink;
import static org.umetadata.service.clients.pipeline.config.WorkflowConfigBuilder.buildDefaultSource;

import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.metadataIngestion.UMetadataWorkflowConfig;
import org.umetadata.schema.metadataIngestion.Sink;
import org.umetadata.schema.metadataIngestion.Source;

public class LineageWorkflowConfig implements WorkflowConfigTypeStrategy {
  public UMetadataWorkflowConfig buildOMWorkflowConfig(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service)
      throws WorkflowBuildException {
    UMetadataWorkflowConfig config = new UMetadataWorkflowConfig();

    Source source = buildDefaultSource(ingestionPipeline, service);
    source.setType(String.format("%s-lineage", source.getType()));

    Sink sink = buildDefaultSink();

    config.setSource(source);
    config.setSink(sink);

    return config;
  }
}
