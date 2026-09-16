package org.umetadata.service.resources.ai;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.ai.CreateAIApplication;
import org.umetadata.schema.entity.ai.AIApplication;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class AIApplicationMapper implements EntityMapper<AIApplication, CreateAIApplication> {
  @Override
  public AIApplication createToEntity(CreateAIApplication create, String user) {
    return copy(new AIApplication(), create, user)
        .withApplicationType(create.getApplicationType())
        .withDevelopmentStage(create.getDevelopmentStage())
        .withModelConfigurations(create.getModelConfigurations())
        .withPrimaryModel(
            create.getPrimaryModel() != null
                ? getEntityReference(Entity.LLM_MODEL, create.getPrimaryModel())
                : null)
        .withPromptTemplates(create.getPromptTemplates())
        .withTools(create.getTools())
        .withDataSources(create.getDataSources())
        .withKnowledgeBases(create.getKnowledgeBases())
        .withUpstreamApplications(create.getUpstreamApplications())
        .withDownstreamApplications(create.getDownstreamApplications())
        .withFramework(create.getFramework())
        .withGovernanceMetadata(create.getGovernanceMetadata())
        .withBiasMetrics(create.getBiasMetrics())
        .withPerformanceMetrics(create.getPerformanceMetrics())
        .withQualityMetrics(create.getQualityMetrics())
        .withSafetyMetrics(create.getSafetyMetrics())
        .withTestSuites(create.getTestSuites())
        .withSourceCode(create.getSourceCode())
        .withDeploymentUrl(create.getDeploymentUrl())
        .withDocumentation(create.getDocumentation());
  }
}
