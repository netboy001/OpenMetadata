package org.umetadata.sdk.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;

public class BulkAPI {

  public static class BulkRequest {
    public List<Object> entities;
    public String operation;
    public Map<String, Object> options;
  }

  public static class BulkResponse {
    public int successCount;
    public int failureCount;
    public List<BulkResult> results;
  }

  public static class BulkResult {
    public String entityId;
    public String status;
    public String error;
    public Object entity;
  }

  public static String bulkCreate(String entityType, List<Object> entities)
      throws UMetadataException {
    return UMetadata.client().bulk().bulkCreate(entityType, entities);
  }

  public static String bulkUpdate(String entityType, List<Object> entities)
      throws UMetadataException {
    return UMetadata.client().bulk().bulkUpdate(entityType, entities);
  }

  public static String bulkDelete(String entityType, List<String> entityIds)
      throws UMetadataException {
    return UMetadata.client().bulk().bulkDelete(entityType, entityIds);
  }

  public static String getBulkOperationStatus(String operationId) throws UMetadataException {
    return UMetadata.client().bulk().getBulkOperationStatus(operationId);
  }

  public static CompletableFuture<String> bulkCreateAsync(
      String entityType, List<Object> entities) {
    return UMetadata.client().bulk().bulkCreateAsync(entityType, entities);
  }

  public static CompletableFuture<String> bulkUpdateAsync(
      String entityType, List<Object> entities) {
    return UMetadata.client().bulk().bulkUpdateAsync(entityType, entities);
  }

  public static CompletableFuture<String> bulkDeleteAsync(
      String entityType, List<String> entityIds) {
    return UMetadata.client().bulk().bulkDeleteAsync(entityType, entityIds);
  }

  public static String bulkAddTags(String entityType, List<String> entityIds, List<String> tags)
      throws UMetadataException {
    return UMetadata.client().bulk().bulkAddTags(entityType, entityIds, tags);
  }

  public static String bulkRemoveTags(String entityType, List<String> entityIds, List<String> tags)
      throws UMetadataException {
    return UMetadata.client().bulk().bulkRemoveTags(entityType, entityIds, tags);
  }
}
