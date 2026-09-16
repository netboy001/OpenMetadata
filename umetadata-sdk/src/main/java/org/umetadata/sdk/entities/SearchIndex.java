package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.data.CreateSearchIndex;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

/**
 * SDK wrapper for SearchIndex operations.
 * This class provides static methods for SearchIndex CRUD operations.
 * It does NOT extend the schema SearchIndex class to avoid naming conflicts.
 */
public class SearchIndex {

  private static UMetadataClient defaultClient;

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  private static UMetadataClient getClient() {
    if (defaultClient == null) {
      return UMetadata.client();
    }
    return defaultClient;
  }

  // Static methods for CRUD operations
  public static org.umetadata.schema.entity.data.SearchIndex create(CreateSearchIndex request)
      throws UMetadataException {
    return getClient().searchIndexes().create(request);
  }

  public static org.umetadata.schema.entity.data.SearchIndex retrieve(String id)
      throws UMetadataException {
    return getClient().searchIndexes().get(id);
  }

  public static org.umetadata.schema.entity.data.SearchIndex retrieve(String id, String fields)
      throws UMetadataException {
    return getClient().searchIndexes().get(id, fields);
  }

  public static org.umetadata.schema.entity.data.SearchIndex retrieve(UUID id)
      throws UMetadataException {
    return retrieve(id.toString());
  }

  public static org.umetadata.schema.entity.data.SearchIndex retrieveByName(String name)
      throws UMetadataException {
    return getClient().searchIndexes().getByName(name);
  }

  public static org.umetadata.schema.entity.data.SearchIndex retrieveByName(
      String name, String fields) throws UMetadataException {
    return getClient().searchIndexes().getByName(name, fields);
  }

  public static org.umetadata.schema.entity.data.SearchIndex update(
      String id, org.umetadata.schema.entity.data.SearchIndex patch)
      throws UMetadataException {
    return getClient().searchIndexes().update(id, patch);
  }

  public static void delete(String id) throws UMetadataException {
    getClient().searchIndexes().delete(id);
  }

  public static void delete(UUID id) throws UMetadataException {
    getClient().searchIndexes().delete(id);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    getClient().searchIndexes().delete(id, params);
  }

  // Async delete methods
  public static CompletableFuture<Void> deleteAsync(String id) {
    return getClient().searchIndexes().deleteAsync(id);
  }

  public static CompletableFuture<Void> deleteAsync(UUID id) {
    return getClient().searchIndexes().deleteAsync(id);
  }

  public static CompletableFuture<Void> deleteAsync(
      String id, boolean recursive, boolean hardDelete) {
    return CompletableFuture.runAsync(
        () -> {
          try {
            delete(id, recursive, hardDelete);
          } catch (UMetadataException e) {
            throw new RuntimeException(e);
          }
        });
  }

  public static ListResponse<org.umetadata.schema.entity.data.SearchIndex> list()
      throws UMetadataException {
    return getClient().searchIndexes().list();
  }

  public static ListResponse<org.umetadata.schema.entity.data.SearchIndex> list(
      ListParams params) throws UMetadataException {
    return getClient().searchIndexes().list(params);
  }
}
