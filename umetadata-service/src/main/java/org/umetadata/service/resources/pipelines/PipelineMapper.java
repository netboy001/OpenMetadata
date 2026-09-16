package org.umetadata.service.resources.pipelines;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreatePipeline;
import org.umetadata.schema.entity.data.Pipeline;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class PipelineMapper implements EntityMapper<Pipeline, CreatePipeline> {
  @Override
  public Pipeline createToEntity(CreatePipeline create, String user) {
    return copy(new Pipeline(), create, user)
        .withService(getEntityReference(Entity.PIPELINE_SERVICE, create.getService()))
        .withState(create.getState())
        .withTasks(create.getTasks())
        .withSourceUrl(create.getSourceUrl())
        .withConcurrency(create.getConcurrency())
        .withStartDate(create.getStartDate())
        .withPipelineLocation(create.getPipelineLocation())
        .withScheduleInterval(create.getScheduleInterval())
        .withSourceHash(create.getSourceHash());
  }
}
