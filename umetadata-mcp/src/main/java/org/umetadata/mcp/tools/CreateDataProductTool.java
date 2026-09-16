package org.umetadata.mcp.tools;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.api.domains.CreateDataProduct;
import org.umetadata.schema.entity.domains.DataProduct;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.MetadataOperation;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.DataProductRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.domains.DataProductMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.ImpersonationContext;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.security.policyevaluator.CreateResourceContext;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.util.RestUtil;

@Slf4j
public class CreateDataProductTool implements McpTool {

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer, CatalogSecurityContext securityContext, Map<String, Object> params) {
    throw new UnsupportedOperationException("CreateDataProductTool requires limit validation.");
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

    final List<String> domains = JsonUtils.readOrConvertValues(params.get("domains"), String.class);
    requireDomains(domains);
    CommonUtils.preflightDomains(domains);
    final List<String> experts = JsonUtils.readOrConvertValues(params.get("experts"), String.class);
    CommonUtils.preflightExperts(experts);

    final CreateDataProduct create = new CreateDataProduct();
    create.setName(name);
    create.setDescription(description);
    create.setDomains(domains);
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
    applyReviewers(create, params);
    if (params.containsKey("tags")) {
      create.setTags(CommonUtils.buildTagLabels(params.get("tags")));
    }

    final DataProductMapper mapper = new DataProductMapper();
    final DataProduct entity =
        mapper.createToEntity(create, CommonUtils.principal(securityContext));

    authorize(authorizer, limits, securityContext, entity);

    final DataProductRepository repo =
        (DataProductRepository) Entity.getEntityRepository(Entity.DATA_PRODUCT);
    repo.prepareInternal(entity, false);

    final String userName = CommonUtils.principal(securityContext);
    final RestUtil.PutResponse<DataProduct> response =
        repo.createOrUpdate(null, entity, userName, ImpersonationContext.getImpersonatedBy());
    McpChangeEventUtil.publishChangeEvent(response.getEntity(), response.getChangeType(), userName);
    return McpResponseUtils.compact(response.getEntity(), response.getChangeType());
  }

  private static void requireDomains(List<String> domains) {
    if (domains == null || domains.isEmpty()) {
      throw new IllegalArgumentException(
          "Parameter 'domains' is required and must list at least one domain FQN. Example:"
              + " ['Finance', 'Marketing'].");
    }
  }

  private static void applyReviewers(CreateDataProduct create, Map<String, Object> params) {
    if (params.containsKey("reviewers")) {
      final List<EntityReference> reviewers = CommonUtils.getTeamsOrUsers(params.get("reviewers"));
      if (!reviewers.isEmpty()) {
        create.setReviewers(reviewers);
      }
    }
  }

  private static void authorize(
      Authorizer authorizer,
      Limits limits,
      CatalogSecurityContext securityContext,
      DataProduct entity) {
    final OperationContext operationContext =
        new OperationContext(Entity.DATA_PRODUCT, MetadataOperation.CREATE);
    final CreateResourceContext<DataProduct> createResourceContext =
        new CreateResourceContext<>(Entity.DATA_PRODUCT, entity);
    limits.enforceLimits(securityContext, createResourceContext, operationContext);
    authorizer.authorize(securityContext, operationContext, createResourceContext);
  }
}
