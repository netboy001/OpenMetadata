package org.umetadata.service.resources.teams;

import static org.umetadata.common.utils.CommonUtil.nullOrEmpty;
import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import org.umetadata.schema.api.teams.CreateRole;
import org.umetadata.schema.entity.teams.Role;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class RoleMapper implements EntityMapper<Role, CreateRole> {
  @Override
  public Role createToEntity(CreateRole create, String user) {
    if (nullOrEmpty(create.getPolicies())) {
      throw new IllegalArgumentException("At least one policy is required to create a role");
    }
    return copy(new Role(), create, user)
        .withPolicies(getEntityReferences(Entity.POLICY, create.getPolicies()));
  }
}
