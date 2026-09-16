package org.umetadata.mcp.tools;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.api.data.CreateGlossary;
import org.umetadata.schema.entity.data.Glossary;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.MetadataOperation;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.GlossaryRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.glossary.GlossaryMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.ImpersonationContext;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.security.policyevaluator.CreateResourceContext;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.util.RestUtil;

@Slf4j
public class GlossaryTool implements McpTool {
  private static GlossaryMapper glossaryMapper = new GlossaryMapper();

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer, CatalogSecurityContext securityContext, Map<String, Object> params) {
    throw new UnsupportedOperationException("GlossaryTool requires limit validation.");
  }

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer,
      Limits limits,
      CatalogSecurityContext securityContext,
      Map<String, Object> params) {
    CreateGlossary createGlossary = new CreateGlossary();
    createGlossary.setName((String) params.get("name"));
    createGlossary.setDescription((String) params.get("description"));
    if (params.containsKey("owners")) {
      CommonUtils.setOwners(createGlossary, params);
    }
    if (params.containsKey("reviewers")) {
      setReviewers(createGlossary, params);
    }
    if (params.containsKey("mutuallyExclusive")) {
      Object meObj = params.get("mutuallyExclusive");
      if (meObj instanceof Boolean b) {
        createGlossary.setMutuallyExclusive(b);
      } else if (meObj instanceof String s) {
        createGlossary.setMutuallyExclusive("true".equalsIgnoreCase(s));
      }
    }

    Glossary glossary =
        glossaryMapper.createToEntity(createGlossary, securityContext.getUserPrincipal().getName());

    // Validate If the User Can Perform the Create Operation
    OperationContext operationContext =
        new OperationContext(Entity.GLOSSARY, MetadataOperation.CREATE);
    CreateResourceContext<Glossary> createResourceContext =
        new CreateResourceContext<>(Entity.GLOSSARY, glossary);
    limits.enforceLimits(securityContext, createResourceContext, operationContext);
    authorizer.authorize(securityContext, operationContext, createResourceContext);

    GlossaryRepository glossaryRepository =
        (GlossaryRepository) Entity.getEntityRepository(Entity.GLOSSARY);

    glossaryRepository.prepareInternal(glossary, false);

    String impersonatedBy = ImpersonationContext.getImpersonatedBy();

    String userName = securityContext.getUserPrincipal().getName();
    RestUtil.PutResponse<Glossary> response =
        glossaryRepository.createOrUpdate(null, glossary, userName, impersonatedBy);
    McpChangeEventUtil.publishChangeEvent(response.getEntity(), response.getChangeType(), userName);
    return McpResponseUtils.compact(response.getEntity(), response.getChangeType());
  }

  public static void setReviewers(CreateGlossary entity, Map<String, Object> params) {
    List<EntityReference> reviewers = CommonUtils.getTeamsOrUsers(params.get("reviewers"));
    if (!reviewers.isEmpty()) {
      entity.setReviewers(reviewers);
    }
  }
}
