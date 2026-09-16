package org.umetadata.service.resources.ai;

import org.umetadata.schema.api.ai.CreatePromptTemplate;
import org.umetadata.schema.entity.ai.PromptTemplate;
import org.umetadata.service.mapper.EntityMapper;

public class PromptTemplateMapper implements EntityMapper<PromptTemplate, CreatePromptTemplate> {
  @Override
  public PromptTemplate createToEntity(CreatePromptTemplate create, String user) {
    return copy(new PromptTemplate(), create, user)
        .withTemplateContent(create.getTemplateContent())
        .withSystemPrompt(create.getSystemPrompt())
        .withVariables(create.getVariables())
        .withExamples(create.getExamples())
        .withTemplateType(
            create.getTemplateType() != null
                ? PromptTemplate.TemplateType.valueOf(create.getTemplateType().name())
                : null)
        .withTemplateVersion(create.getTemplateVersion());
  }
}
