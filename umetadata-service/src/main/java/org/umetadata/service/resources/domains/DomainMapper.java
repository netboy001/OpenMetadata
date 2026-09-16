package org.umetadata.service.resources.domains;

import static org.umetadata.service.util.EntityUtil.getEntityReference;
import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import java.util.List;
import org.umetadata.schema.api.domains.CreateDomain;
import org.umetadata.schema.entity.domains.Domain;
import org.umetadata.schema.type.Include;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.EntityUtil;

public class DomainMapper implements EntityMapper<Domain, CreateDomain> {
  @Override
  public Domain createToEntity(CreateDomain create, String user) {
    List<String> experts = create.getExperts();
    return copy(new Domain(), create, user)
        .withStyle(create.getStyle())
        .withDomainType(create.getDomainType())
        .withFullyQualifiedName(create.getName())
        .withParent(
            Entity.getEntityReference(
                getEntityReference(Entity.DOMAIN, create.getParent()), Include.NON_DELETED))
        .withExperts(
            EntityUtil.validateAndPopulateEntityReferences(
                getEntityReferences(Entity.USER, experts)));
  }
}
