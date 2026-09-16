package org.umetadata.service.resources.services.llm;

import org.umetadata.schema.api.services.CreateLLMService;
import org.umetadata.schema.entity.services.LLMService;
import org.umetadata.service.mapper.EntityMapper;

public class LLMServiceMapper implements EntityMapper<LLMService, CreateLLMService> {
  @Override
  public LLMService createToEntity(CreateLLMService create, String user) {
    return copy(new LLMService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
