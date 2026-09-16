package org.umetadata.service.resources.ai;

import org.umetadata.schema.api.ai.CreateAIGovernancePolicy;
import org.umetadata.schema.entity.ai.AIGovernancePolicy;
import org.umetadata.service.mapper.EntityMapper;

public class AIGovernancePolicyMapper
    implements EntityMapper<AIGovernancePolicy, CreateAIGovernancePolicy> {
  @Override
  public AIGovernancePolicy createToEntity(CreateAIGovernancePolicy create, String user) {
    return copy(new AIGovernancePolicy(), create, user)
        .withPolicyType(create.getPolicyType())
        .withRules(create.getRules())
        .withBiasThresholds(create.getBiasThresholds())
        .withDataAccessControls(create.getDataAccessControls())
        .withCostControls(create.getCostControls())
        .withComplianceRequirements(create.getComplianceRequirements())
        .withPerformanceStandards(create.getPerformanceStandards())
        .withAppliesTo(create.getAppliesTo())
        .withEnforcementLevel(
            create.getEnforcementLevel() != null
                ? AIGovernancePolicy.EnforcementLevel.valueOf(create.getEnforcementLevel().name())
                : null)
        .withEnabled(create.getEnabled());
  }
}
