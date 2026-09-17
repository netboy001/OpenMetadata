package org.umetadata.service.resources.services.ingestionpipelines;

import org.umetadata.schema.api.services.ingestionPipelines.CreateIngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.UMetadataConnectionBuilder;

public class IngestionPipelineMapper
    implements EntityMapper<IngestionPipeline, CreateIngestionPipeline> {
  private final UMetadataApplicationConfig uMetadataApplicationConfig;

  public IngestionPipelineMapper(UMetadataApplicationConfig uMetadataApplicationConfig) {
    this.uMetadataApplicationConfig = uMetadataApplicationConfig;
  }

  @Override
  public IngestionPipeline createToEntity(CreateIngestionPipeline create, String user) {
    UMetadataConnection uMetadataServerConnection =
        new UMetadataConnectionBuilder(uMetadataApplicationConfig).build();

    return copy(new IngestionPipeline(), create, user)
        .withPipelineType(create.getPipelineType())
        .withAirflowConfig(create.getAirflowConfig())
        .withuMetadataServerConnection(uMetadataServerConnection)
        .withSourceConfig(create.getSourceConfig())
        .withLoggerLevel(create.getLoggerLevel())
        .withRaiseOnError(create.getRaiseOnError())
        .withProvider(create.getProvider())
        .withService(create.getService())
        .withEnableStreamableLogs(create.getEnableStreamableLogs())
        .withProcessingEngine(create.getProcessingEngine());
  }
}
