package org.umetadata.service.resources.policies;

import org.umetadata.schema.api.policies.CreatePolicy;
import org.umetadata.schema.entity.policies.Policy;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.service.mapper.EntityMapper;

public class PolicyMapper implements EntityMapper<Policy, CreatePolicy> {
  @Override
  public Policy createToEntity(CreatePolicy create, String user) {
    Policy policy =
        copy(new Policy(), create, user)
            .withRules(create.getRules())
            .withEnabled(create.getEnabled());
    if (create.getLocation() != null) {
      policy = policy.withLocation(new EntityReference().withId(create.getLocation()));
    }
    return policy;
  }
}
