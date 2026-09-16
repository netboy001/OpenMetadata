package org.umetadata.sdk.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import java.util.Map;
import java.util.UUID;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.network.RequestOptions;

public abstract class BaseResource<T> {
  protected final HttpClient httpClient;
  protected final String basePath;
  protected final ObjectMapper objectMapper;

  protected BaseResource(HttpClient httpClient, String basePath) {
    this.httpClient = httpClient;
    this.basePath = basePath;
    this.objectMapper = new ObjectMapper();
    // Configure to include null values to ensure proper patch generation
    this.objectMapper.setSerializationInclusion(
        com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS);
  }

  public T create(T entity) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, entity, getEntityClass());
  }

  public T get(UUID id) throws UMetadataException {
    return get(id.toString());
  }

  public T get(String id) throws UMetadataException {
    return httpClient.execute(HttpMethod.GET, basePath + "/" + id, null, getEntityClass());
  }

  public T get(String id, String fields) throws UMetadataException {
    RequestOptions options = RequestOptions.builder().queryParam("fields", fields).build();
    return httpClient.execute(HttpMethod.GET, basePath + "/" + id, null, getEntityClass(), options);
  }

  public T getByName(String name) throws UMetadataException {
    return httpClient.execute(HttpMethod.GET, basePath + "/name/" + name, null, getEntityClass());
  }

  public T getByName(String name, String fields) throws UMetadataException {
    RequestOptions options = RequestOptions.builder().queryParam("fields", fields).build();
    return httpClient.execute(
        HttpMethod.GET, basePath + "/name/" + name, null, getEntityClass(), options);
  }

  public ListResponse<T> list() throws UMetadataException {
    return list(new ListParams());
  }

  public ListResponse<T> list(ListParams params) throws UMetadataException {
    RequestOptions options = RequestOptions.builder().queryParams(params.toQueryParams()).build();
    return httpClient.execute(HttpMethod.GET, basePath, null, getListResponseClass(), options);
  }

  public T update(UUID id, T entity) throws UMetadataException {
    return update(id.toString(), entity, null);
  }

  public T update(String id, T entity) throws UMetadataException {
    return update(id, entity, null);
  }

  public T update(UUID id, T entity, String etag) throws UMetadataException {
    return update(id.toString(), entity, etag);
  }

  public T update(String id, T entity, String etag) throws UMetadataException {
    try {
      // Fetch the original entity WITHOUT extra fields to avoid issues with fields that may not
      // exist
      T original = get(id);

      // Convert both to JSON strings
      String originalJson = objectMapper.writeValueAsString(original);
      String updatedJson = objectMapper.writeValueAsString(entity);

      // Generate JSON Patch document
      JsonNode originalNode = objectMapper.readTree(originalJson);
      JsonNode updatedNode = objectMapper.readTree(updatedJson);
      JsonNode patch = JsonDiff.asJson(originalNode, updatedNode);

      // Build request options with ETag if provided
      RequestOptions options = null;
      if (etag != null) {
        options = RequestOptions.builder().header("If-Match", etag).build();
      }

      // Send PATCH request with the JSON Patch document
      return httpClient.execute(
          HttpMethod.PATCH, basePath + "/" + id, patch, getEntityClass(), options);
    } catch (Exception e) {
      throw new UMetadataException("Failed to update entity: " + e.getMessage(), e);
    }
  }

  public T upsert(T entity) throws UMetadataException {
    // PUT without ID for create-or-update operations
    return httpClient.execute(HttpMethod.PUT, basePath, entity, getEntityClass());
  }

  public T patch(UUID id, JsonNode patchDocument) throws UMetadataException {
    return patch(id.toString(), patchDocument, null);
  }

  public T patch(String id, JsonNode patchDocument) throws UMetadataException {
    return patch(id, patchDocument, null);
  }

  public T patch(UUID id, JsonNode patchDocument, String etag) throws UMetadataException {
    return patch(id.toString(), patchDocument, etag);
  }

  public T patch(String id, JsonNode patchDocument, String etag) throws UMetadataException {
    // Build request options with ETag if provided
    RequestOptions options = null;
    if (etag != null) {
      options = RequestOptions.builder().header("If-Match", etag).build();
    }
    return httpClient.execute(
        HttpMethod.PATCH, basePath + "/" + id, patchDocument, getEntityClass(), options);
  }

  public void delete(UUID id) throws UMetadataException {
    delete(id.toString());
  }

  public void delete(String id) throws UMetadataException {
    httpClient.execute(HttpMethod.DELETE, basePath + "/" + id, null, Void.class);
  }

  public void delete(String id, Map<String, String> params) throws UMetadataException {
    RequestOptions options = RequestOptions.builder().queryParams(params).build();
    httpClient.execute(HttpMethod.DELETE, basePath + "/" + id, null, Void.class, options);
  }

  protected abstract Class<T> getEntityClass();

  @SuppressWarnings("unchecked")
  protected Class<ListResponse<T>> getListResponseClass() {
    return (Class<ListResponse<T>>) (Class<?>) ListResponse.class;
  }
}
