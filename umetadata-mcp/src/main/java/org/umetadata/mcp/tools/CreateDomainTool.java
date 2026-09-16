package org.umetadata.mcp.tools;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.api.domains.CreateDomain;
import org.umetadata.schema.entity.domains.Domain;
import org.umetadata.schema.type.MetadataOperation;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.DomainRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.domains.DomainMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.ImpersonationContext;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.security.policyevaluator.CreateResourceContext;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.util.RestUtil;

@Slf4j
public class CreateDomainTool implements McpTool {
  private static final String DEFAULT_DOMAIN_TYPE = "Aggregate";

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer, CatalogSecurityContext securityContext, Map<String, Object> params) {
    throw new UnsupportedOperationException("CreateDomainTool requires limit validation.");
  }

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer,
      Limits limits,
      CatalogSecurityContext securityContext,
      Map<String, Object> params) {
    final String name = CommonUtils.requireNonBlank(params.get("name"), "name");
    final String description =
        CommonUtils.requireNonBlank(params.get("description"), "description");

    final String parent = CommonUtils.optString(params, "parent");
    preflightParent(parent);
    final List<String> experts = JsonUtils.readOrConvertValues(params.get("experts"), String.class);
    CommonUtils.preflightExperts(experts);

    final CreateDomain create = new CreateDomain();
    create.setName(name);
    create.setDescription(description);
    create.setDomainType(parseDomainType(params.get("domainType")));
    if (parent != null) {
      create.setParent(parent);
    }
    if (!experts.isEmpty()) {
      create.setExperts(experts);
    }
    final String displayName = CommonUtils.optString(params, "displayName");
    if (displayName != null) {
      create.setDisplayName(displayName);
    }
    if (params.containsKey("owners")) {
      CommonUtils.setOwners(create, params);
    }
    if (params.containsKey("tags")) {
      create.setTags(CommonUtils.buildTagLabels(params.get("tags")));
    }

    final DomainMapper mapper = new DomainMapper();
    final Domain entity = mapper.createToEntity(create, CommonUtils.principal(securityContext));

    authorize(authorizer, limits, securityContext, entity);

    final DomainRepository repo = (DomainRepository) Entity.getEntityRepository(Entity.DOMAIN);
    repo.prepareInternal(entity, false);

    final String userName = CommonUtils.principal(securityContext);
    final RestUtil.PutResponse<Domain> response =
        repo.createOrUpdate(null, entity, userName, ImpersonationContext.getImpersonatedBy());
    McpChangeEventUtil.publishChangeEvent(response.getEntity(), response.getChangeType(), userName);
    return McpResponseUtils.compact(response.getEntity(), response.getChangeType());
  }

  static CreateDomain.DomainType parseDomainType(Object raw) {
    final String value = (raw instanceof String s && !s.isBlank()) ? s : DEFAULT_DOMAIN_TYPE;
    CreateDomain.DomainType result;
    try {
      result = CreateDomain.DomainType.fromValue(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Parameter 'domainType' has invalid value '"
              + value
              + "'. Valid values are: Source-aligned, Consumer-aligned, Aggregate");
    }
    return result;
  }

  private static void preflightParent(String parent) {
    if (parent != null && !parent.isBlank()) {
      CommonUtils.requireExists(
          Entity.DOMAIN,
          parent,
          "Parent domain '"
              + parent
              + "' not found. The parent domain must already exist before creating a child"
              + " domain.");
    }
  }

  private static void authorize(
      Authorizer authorizer, Limits limits, CatalogSecurityContext securityContext, Domain entity) {
    final OperationContext operationContext =
        new OperationContext(Entity.DOMAIN, MetadataOperation.CREATE);
    final CreateResourceContext<Domain> createResourceContext =
        new CreateResourceContext<>(Entity.DOMAIN, entity);
    limits.enforceLimits(securityContext, createResourceContext, operationContext);
    authorizer.authorize(securityContext, operationContext, createResourceContext);
  }
}
