package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.classification.CreateTag;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * SDK wrapper for Tag operations.
 * This class provides static methods for Tag CRUD operations.
 */
public class Tag {
  private static UMetadataClient defaultClient;

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  private static UMetadataClient getClient() {
    if (defaultClient == null) {
      throw new IllegalStateException("Default client not set. Call setDefaultClient() first.");
    }
    return defaultClient;
  }

  // Static CRUD methods
  public static org.umetadata.schema.entity.classification.Tag create(CreateTag request) {
    return getClient().tags().create(request);
  }

  public static org.umetadata.schema.entity.classification.Tag retrieve(String id) {
    return getClient().tags().get(id);
  }

  public static org.umetadata.schema.entity.classification.Tag retrieve(
      String id, String fields) {
    return getClient().tags().get(id, fields);
  }

  public static org.umetadata.schema.entity.classification.Tag retrieveByName(String name) {
    return getClient().tags().getByName(name);
  }

  public static org.umetadata.schema.entity.classification.Tag retrieveByName(
      String name, String fields) {
    return getClient().tags().getByName(name, fields);
  }

  public static org.umetadata.schema.entity.classification.Tag update(
      String id, org.umetadata.schema.entity.classification.Tag entity) {
    return getClient().tags().update(id, entity);
  }

  public static org.umetadata.schema.entity.classification.Tag update(
      org.umetadata.schema.entity.classification.Tag entity) {
    if (entity.getId() == null) {
      throw new IllegalArgumentException("Tag must have an ID for update");
    }
    return update(entity.getId().toString(), entity);
  }

  public static void delete(String id) {
    delete(id, false, false);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete) {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    getClient().tags().delete(id, params);
  }

  // Async operations
  public static CompletableFuture<org.umetadata.schema.entity.classification.Tag> createAsync(
      CreateTag request) {
    return CompletableFuture.supplyAsync(() -> create(request));
  }

  public static CompletableFuture<org.umetadata.schema.entity.classification.Tag> retrieveAsync(
      String id) {
    return CompletableFuture.supplyAsync(() -> retrieve(id));
  }

  public static CompletableFuture<Void> deleteAsync(
      String id, boolean recursive, boolean hardDelete) {
    return CompletableFuture.runAsync(() -> delete(id, recursive, hardDelete));
  }
}
