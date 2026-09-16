package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.data.CreateGlossaryTerm;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * SDK wrapper for GlossaryTerm operations.
 * This class provides static methods for GlossaryTerm CRUD operations.
 */
public class GlossaryTerm {
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
  public static org.umetadata.schema.entity.data.GlossaryTerm create(
      CreateGlossaryTerm request) {
    return getClient().glossaryTerms().create(request);
  }

  public static org.umetadata.schema.entity.data.GlossaryTerm retrieve(String id) {
    return getClient().glossaryTerms().get(id);
  }

  public static org.umetadata.schema.entity.data.GlossaryTerm retrieve(
      String id, String fields) {
    return getClient().glossaryTerms().get(id, fields);
  }

  public static org.umetadata.schema.entity.data.GlossaryTerm retrieveByName(String name) {
    return getClient().glossaryTerms().getByName(name);
  }

  public static org.umetadata.schema.entity.data.GlossaryTerm retrieveByName(
      String name, String fields) {
    return getClient().glossaryTerms().getByName(name, fields);
  }

  public static org.umetadata.schema.entity.data.GlossaryTerm update(
      String id, org.umetadata.schema.entity.data.GlossaryTerm entity) {
    return getClient().glossaryTerms().update(id, entity);
  }

  public static org.umetadata.schema.entity.data.GlossaryTerm update(
      org.umetadata.schema.entity.data.GlossaryTerm entity) {
    if (entity.getId() == null) {
      throw new IllegalArgumentException("GlossaryTerm must have an ID for update");
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
    getClient().glossaryTerms().delete(id, params);
  }

  // Async operations
  public static CompletableFuture<org.umetadata.schema.entity.data.GlossaryTerm> createAsync(
      CreateGlossaryTerm request) {
    return CompletableFuture.supplyAsync(() -> create(request));
  }

  public static CompletableFuture<org.umetadata.schema.entity.data.GlossaryTerm> retrieveAsync(
      String id) {
    return CompletableFuture.supplyAsync(() -> retrieve(id));
  }

  public static CompletableFuture<Void> deleteAsync(
      String id, boolean recursive, boolean hardDelete) {
    return CompletableFuture.runAsync(() -> delete(id, recursive, hardDelete));
  }
}
