package org.umetadata.sdk.services.bulk;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;

public class BulkAPI {
  private final HttpClient httpClient;

  public BulkAPI(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public String bulkCreate(String entityType, List<Object> entities) throws UMetadataException {
    return httpClient.executeForString(
        HttpMethod.POST, String.format("/v1/bulk/%s", entityType), entities);
  }

  public String bulkUpdate(String entityType, List<Object> entities) throws UMetadataException {
    return httpClient.executeForString(
        HttpMethod.PUT, String.format("/v1/bulk/%s", entityType), entities);
  }

  public String bulkDelete(String entityType, List<String> entityIds) throws UMetadataException {
    Map<String, List<String>> requestBody = Map.of("entityIds", entityIds);
    return httpClient.executeForString(
        HttpMethod.DELETE, String.format("/v1/bulk/%s", entityType), requestBody);
  }

  public CompletableFuture<String> bulkCreateAsync(String entityType, List<Object> entities) {
    return httpClient.executeForStringAsync(
        HttpMethod.POST, String.format("/v1/bulk/%s", entityType), entities);
  }

  public CompletableFuture<String> bulkUpdateAsync(String entityType, List<Object> entities) {
    return httpClient.executeForStringAsync(
        HttpMethod.PUT, String.format("/v1/bulk/%s", entityType), entities);
  }

  public CompletableFuture<String> bulkDeleteAsync(String entityType, List<String> entityIds) {
    Map<String, List<String>> requestBody = Map.of("entityIds", entityIds);
    return httpClient.executeForStringAsync(
        HttpMethod.DELETE, String.format("/v1/bulk/%s", entityType), requestBody);
  }

  public String getBulkOperationStatus(String operationId) throws UMetadataException {
    return httpClient.executeForString(
        HttpMethod.GET, String.format("/v1/bulk/status/%s", operationId), null);
  }

  public String bulkAddTags(String entityType, List<String> entityIds, List<String> tags)
      throws UMetadataException {
    Map<String, Object> requestBody =
        Map.of(
            "entityIds", entityIds,
            "tags", tags);
    return httpClient.executeForString(
        HttpMethod.POST, String.format("/v1/bulk/%s/tags", entityType), requestBody);
  }

  public String bulkRemoveTags(String entityType, List<String> entityIds, List<String> tags)
      throws UMetadataException {
    Map<String, Object> requestBody =
        Map.of(
            "entityIds", entityIds,
            "tags", tags);
    return httpClient.executeForString(
        HttpMethod.DELETE, String.format("/v1/bulk/%s/tags", entityType), requestBody);
  }
}
