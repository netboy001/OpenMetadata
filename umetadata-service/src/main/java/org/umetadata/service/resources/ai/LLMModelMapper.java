package org.umetadata.service.resources.ai;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.ai.CreateLLMModel;
import org.umetadata.schema.entity.ai.LLMModel;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class LLMModelMapper implements EntityMapper<LLMModel, CreateLLMModel> {
  @Override
  public LLMModel createToEntity(CreateLLMModel create, String user) {
    return copy(new LLMModel(), create, user)
        .withService(getEntityReference(Entity.LLM_SERVICE, create.getService()))
        .withBaseModel(create.getBaseModel())
        .withModelVersion(create.getModelVersion())
        .withModelProvider(create.getModelProvider())
        .withModelSpecifications(create.getModelSpecifications())
        .withTrainingMetadata(create.getTrainingMetadata())
        .withModelEvaluation(create.getModelEvaluation())
        .withCostMetrics(create.getCostMetrics())
        .withDeploymentInfo(create.getDeploymentInfo())
        .withGovernanceStatus(
            create.getGovernanceStatus() != null
                ? LLMModel.GovernanceStatus.valueOf(create.getGovernanceStatus().name())
                : null);
  }
}
