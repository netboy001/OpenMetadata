package org.umetadata.service.resources.services.security;

import org.umetadata.schema.api.services.CreateSecurityService;
import org.umetadata.schema.entity.services.SecurityService;
import org.umetadata.service.mapper.EntityMapper;

public class SecurityServiceMapper implements EntityMapper<SecurityService, CreateSecurityService> {
  @Override
  public SecurityService createToEntity(CreateSecurityService create, String user) {
    return copy(new SecurityService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection());
  }
}
