package org.umetadata.mcp.tools;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.entity.data.GlossaryTerm;
import org.umetadata.schema.type.MetadataOperation;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.GlossaryTermRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.glossary.GlossaryTermMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.ImpersonationContext;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.security.policyevaluator.CreateResourceContext;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.util.RestUtil;

@Slf4j
public class GlossaryTermTool implements McpTool {
  private static GlossaryTermMapper glossaryTermMapper = new GlossaryTermMapper();

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer, CatalogSecurityContext securityContext, Map<String, Object> params) {
    throw new UnsupportedOperationException("GlossaryTermTool requires limit validation.");
  }

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer,
      Limits limits,
      CatalogSecurityContext securityContext,
      Map<String, Object> params) {
    org.umetadata.schema.api.data.CreateGlossaryTerm createGlossaryTerm =
        new org.umetadata.schema.api.data.CreateGlossaryTerm();
    createGlossaryTerm.setName((String) params.get("name"));
    createGlossaryTerm.setGlossary((String) params.get("glossary"));
    createGlossaryTerm.setParent((String) params.get("parentTerm"));
    createGlossaryTerm.setDescription((String) params.get("description"));
    if (params.containsKey("owners")) {
      CommonUtils.setOwners(createGlossaryTerm, params);
    }
    if (params.containsKey("reviewers")) {
      createGlossaryTerm.setReviewers(CommonUtils.getTeamsOrUsers(params.get("reviewers")));
    }

    GlossaryTerm glossaryTerm =
        glossaryTermMapper.createToEntity(
            createGlossaryTerm, securityContext.getUserPrincipal().getName());

    // Validate If the User Can Perform the Create Operation
    OperationContext operationContext =
        new OperationContext(Entity.GLOSSARY_TERM, MetadataOperation.CREATE);
    CreateResourceContext<GlossaryTerm> createResourceContext =
        new CreateResourceContext<>(Entity.GLOSSARY_TERM, glossaryTerm);
    limits.enforceLimits(securityContext, createResourceContext, operationContext);
    authorizer.authorize(securityContext, operationContext, createResourceContext);

    GlossaryTermRepository glossaryTermRepository =
        (GlossaryTermRepository) Entity.getEntityRepository(Entity.GLOSSARY_TERM);
    glossaryTermRepository.prepareInternal(glossaryTerm, false);

    String impersonatedBy = ImpersonationContext.getImpersonatedBy();

    String userName = securityContext.getUserPrincipal().getName();
    RestUtil.PutResponse<GlossaryTerm> response =
        glossaryTermRepository.createOrUpdate(null, glossaryTerm, userName, impersonatedBy);
    McpChangeEventUtil.publishChangeEvent(response.getEntity(), response.getChangeType(), userName);
    return McpResponseUtils.compact(response.getEntity(), response.getChangeType());
  }
}
