package org.umetadata.service.resources.services.pipeline;

import org.umetadata.schema.api.services.CreatePipelineService;
import org.umetadata.schema.entity.services.PipelineService;
import org.umetadata.service.mapper.EntityMapper;

public class PipelineServiceMapper implements EntityMapper<PipelineService, CreatePipelineService> {
  @Override
  public PipelineService createToEntity(CreatePipelineService create, String user) {
    return copy(new PipelineService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
