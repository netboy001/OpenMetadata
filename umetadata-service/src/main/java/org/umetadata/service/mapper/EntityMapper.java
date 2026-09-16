package org.umetadata.service.mapper;

import static org.umetadata.schema.type.Include.NON_DELETED;
import static org.umetadata.service.jdbi3.EntityRepository.validateOwners;
import static org.umetadata.service.jdbi3.EntityRepository.validateReviewers;
import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import java.util.List;
import java.util.UUID;
import org.umetadata.common.utils.CommonUtil;
import org.umetadata.schema.CreateEntity;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.service.Entity;
import org.umetadata.service.rules.RuleEngine;

public interface EntityMapper<T extends EntityInterface, C extends CreateEntity> {
  T createToEntity(C create, String user);

  default T copy(T entity, CreateEntity request, String updatedBy) {
    List<EntityReference> owners = validateOwners(request.getOwners());
    List<EntityReference> domains = validateDomains(request.getDomains());
    validateReviewers(request.getReviewers());
    entity.setId(UUID.randomUUID());
    entity.setName(request.getName());
    entity.setDisplayName(request.getDisplayName());
    entity.setDescription(request.getDescription());
    entity.setOwners(owners);
    entity.setDomains(domains);
    entity.setTags(request.getTags());
    entity.setDataProducts(getEntityReferences(Entity.DATA_PRODUCT, request.getDataProducts()));
    entity.setLifeCycle(request.getLifeCycle());
    entity.setExtension(request.getExtension());
    entity.setUpdatedBy(updatedBy);
    entity.setUpdatedAt(System.currentTimeMillis());
    entity.setReviewers(request.getReviewers());

    RuleEngine.getInstance().evaluate(entity);
    return entity;
  }

  default List<EntityReference> validateDomains(List<String> domainFqns) {
    if (CommonUtil.nullOrEmpty(domainFqns)) {
      return null;
    }
    return domainFqns.stream()
        .map(domainFqn -> Entity.getEntityReferenceByName(Entity.DOMAIN, domainFqn, NON_DELETED))
        .toList();
  }
}
