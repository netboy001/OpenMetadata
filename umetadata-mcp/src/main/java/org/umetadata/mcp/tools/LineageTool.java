package org.umetadata.mcp.tools;

import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.api.lineage.AddLineage;
import org.umetadata.schema.type.EntitiesEdge;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.MetadataOperation;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.security.policyevaluator.ResourceContext;

@Slf4j
public class LineageTool implements McpTool {
  @Override
  public Map<String, Object> execute(
      Authorizer authorizer,
      CatalogSecurityContext catalogSecurityContext,
      Map<String, Object> params) {
    EntityReference fromEntity =
        JsonUtils.convertValue(params.get("fromEntity"), EntityReference.class);
    EntityReference toEntity =
        JsonUtils.convertValue(params.get("toEntity"), EntityReference.class);

    if (fromEntity == null || fromEntity.getType() == null || fromEntity.getId() == null) {
      throw new IllegalArgumentException(
          "Parameter 'fromEntity' is required and must include 'type' and 'id'");
    }
    if (toEntity == null || toEntity.getType() == null || toEntity.getId() == null) {
      throw new IllegalArgumentException(
          "Parameter 'toEntity' is required and must include 'type' and 'id'");
    }

    authorizer.authorize(
        catalogSecurityContext,
        new OperationContext(fromEntity.getType(), MetadataOperation.EDIT_LINEAGE),
        new ResourceContext<>(fromEntity.getType(), fromEntity.getId(), fromEntity.getName()));
    authorizer.authorize(
        catalogSecurityContext,
        new OperationContext(toEntity.getType(), MetadataOperation.EDIT_LINEAGE),
        new ResourceContext<>(toEntity.getType(), toEntity.getId(), toEntity.getName()));

    LOG.info(
        "Creating lineage edge from {}.{} to {}.{}",
        fromEntity.getType(),
        fromEntity.getName(),
        toEntity.getType(),
        toEntity.getName());

    AddLineage lineage =
        new AddLineage()
            .withEdge(new EntitiesEdge().withFromEntity(fromEntity).withToEntity(toEntity));
    String updatedBy = catalogSecurityContext.getUserPrincipal().getName();
    Entity.getLineageRepository().addLineage(lineage, updatedBy);
    return Map.of("result", "Lineage Edge created successfully");
  }

  @Override
  public Map<String, Object> execute(
      Authorizer authorizer,
      Limits limits,
      CatalogSecurityContext catalogSecurityContext,
      Map<String, Object> map)
      throws IOException {
    throw new UnsupportedOperationException("LineageTool does not require limit validation.");
  }
}
