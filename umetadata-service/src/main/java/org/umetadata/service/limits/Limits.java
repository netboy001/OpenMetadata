package org.umetadata.service.limits;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jdbi.v3.core.Jdbi;
import org.umetadata.schema.system.LimitsConfig;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.exception.BulkLimitException;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.security.policyevaluator.ResourceContextInterface;

public interface Limits {
  void init(UMetadataApplicationConfig serverConfig, Jdbi jdbi);

  void enforceLimits(
      SecurityContext securityContext,
      ResourceContextInterface resourceContext,
      OperationContext operationContext);

  default void enforceBulkSizeLimit(String entityType, int bulkSize) {
    if (bulkSize > 100) {
      throw new BulkLimitException(
          "Bulk size limit per request reached for entity type: " + entityType);
    }
  }

  LimitsConfig getLimitsConfig();

  Response getLimitsForaFeature(String entityType, boolean cache);

  void invalidateCache(String entityType);
}
