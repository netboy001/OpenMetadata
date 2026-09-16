package org.umetadata.service.clients.pipeline.config.types;

import static org.umetadata.service.clients.pipeline.config.WorkflowConfigBuilder.buildDefaultSink;
import static org.umetadata.service.clients.pipeline.config.WorkflowConfigBuilder.buildDefaultSource;

import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.metadataIngestion.UMetadataWorkflowConfig;
import org.umetadata.schema.metadataIngestion.Processor;
import org.umetadata.schema.metadataIngestion.Sink;
import org.umetadata.schema.metadataIngestion.Source;
import org.umetadata.schema.services.connections.metadata.ComponentConfig;

public class AutoClassificationWorkflowConfig implements WorkflowConfigTypeStrategy {
  public UMetadataWorkflowConfig buildOMWorkflowConfig(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service)
      throws WorkflowBuildException {
    UMetadataWorkflowConfig config = new UMetadataWorkflowConfig();

    Source source = buildDefaultSource(ingestionPipeline, service);
    Processor processor =
        new Processor().withType("tag-pii-processor").withConfig(new ComponentConfig());
    Sink sink = buildDefaultSink();

    config.setSource(source);
    config.setProcessor(processor);
    config.setSink(sink);

    return config;
  }
}
