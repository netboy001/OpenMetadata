package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.data.CreateGlossary;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * SDK wrapper for Glossary operations.
 * This class provides static methods for Glossary CRUD operations.
 */
public class Glossary {
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
  public static org.umetadata.schema.entity.data.Glossary create(CreateGlossary request) {
    return getClient().glossaries().create(request);
  }

  public static org.umetadata.schema.entity.data.Glossary retrieve(String id) {
    return getClient().glossaries().get(id);
  }

  public static org.umetadata.schema.entity.data.Glossary retrieve(String id, String fields) {
    return getClient().glossaries().get(id, fields);
  }

  public static org.umetadata.schema.entity.data.Glossary retrieveByName(String name) {
    return getClient().glossaries().getByName(name);
  }

  public static org.umetadata.schema.entity.data.Glossary retrieveByName(
      String name, String fields) {
    return getClient().glossaries().getByName(name, fields);
  }

  public static org.umetadata.schema.entity.data.Glossary update(
      String id, org.umetadata.schema.entity.data.Glossary entity) {
    return getClient().glossaries().update(id, entity);
  }

  public static org.umetadata.schema.entity.data.Glossary update(
      org.umetadata.schema.entity.data.Glossary entity) {
    if (entity.getId() == null) {
      throw new IllegalArgumentException("Glossary must have an ID for update");
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
    getClient().glossaries().delete(id, params);
  }

  // Async operations
  public static CompletableFuture<org.umetadata.schema.entity.data.Glossary> createAsync(
      CreateGlossary request) {
    return CompletableFuture.supplyAsync(() -> create(request));
  }

  public static CompletableFuture<org.umetadata.schema.entity.data.Glossary> retrieveAsync(
      String id) {
    return CompletableFuture.supplyAsync(() -> retrieve(id));
  }

  public static CompletableFuture<Void> deleteAsync(
      String id, boolean recursive, boolean hardDelete) {
    return CompletableFuture.runAsync(() -> delete(id, recursive, hardDelete));
  }
}
