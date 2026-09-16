package org.umetadata.mcp.tools;

import static org.umetadata.common.utils.CommonUtil.nullOrEmpty;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonPatch;
import java.io.StringReader;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.type.change.ChangeSource;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.EntityRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.ImpersonationContext;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.security.policyevaluator.ResourceContext;
import org.umetadata.service.util.RestUtil;

@Slf4j
public class PatchEntityTool implements McpTool {
  @Override
  public Map<String, Object> execute(
      Authorizer authorizer, CatalogSecurityContext securityContext, Map<String, Object> params) {
    String entityType = (String) params.get("entityType");
    String fqn = (String) params.get("fqn");
    String jsonPatchString = (String) params.get("patch");
    if (nullOrEmpty(jsonPatchString)) {
      throw new IllegalArgumentException("Patch cannot be null or empty");
    }

    JsonArray patchArray = Json.createReader(new StringReader(jsonPatchString)).readArray();
    JsonPatch jsonPatch = Json.createPatch(patchArray);

    // Validate If the User Can Perform the Patch Operation
    OperationContext operationContext = new OperationContext(entityType, jsonPatch);
    authorizer.authorize(
        securityContext, operationContext, new ResourceContext<>(entityType, null, fqn));

    EntityRepository<? extends EntityInterface> repository = Entity.getEntityRepository(entityType);

    String userName = securityContext.getUserPrincipal().getName();
    String impersonatedBy = ImpersonationContext.getImpersonatedBy();
    RestUtil.PatchResponse<? extends EntityInterface> response =
        repository.patch(null, fqn, userName, jsonPatch, ChangeSource.MANUAL, null, impersonatedBy);
    McpChangeEventUtil.publishChangeEvent(response.entity(), response.changeType(), userName);
    return JsonUtils.convertValue(response, Map.class);
  }

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer,
      Limits limits,
      CatalogSecurityContext securityContext,
      Map<String, Object> params) {
    throw new UnsupportedOperationException("PatchEntityTool does not support limits enforcement.");
  }
}
