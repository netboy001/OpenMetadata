package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.services.CreateDatabaseService;
import org.umetadata.sdk.client.UMetadataClient;

public class DatabaseService extends org.umetadata.schema.entity.services.DatabaseService {
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
  public static org.umetadata.schema.entity.services.DatabaseService create(
      CreateDatabaseService request) {
    return getClient().databaseServices().create(request);
  }

  public static org.umetadata.schema.entity.services.DatabaseService retrieve(String id) {
    return getClient().databaseServices().get(id);
  }

  public static org.umetadata.schema.entity.services.DatabaseService retrieve(
      String id, String fields) {
    return getClient().databaseServices().get(id, fields);
  }

  public static org.umetadata.schema.entity.services.DatabaseService retrieveByName(
      String name) {
    return getClient().databaseServices().getByName(name);
  }

  public static org.umetadata.schema.entity.services.DatabaseService retrieveByName(
      String name, String fields) {
    return getClient().databaseServices().getByName(name, fields);
  }

  public static org.umetadata.schema.entity.services.DatabaseService update(
      String id, org.umetadata.schema.entity.services.DatabaseService service) {
    return getClient().databaseServices().update(id, service);
  }

  public static org.umetadata.schema.entity.services.DatabaseService update(
      org.umetadata.schema.entity.services.DatabaseService service) {
    if (service.getId() == null) {
      throw new IllegalArgumentException("DatabaseService must have an ID for update");
    }
    return update(service.getId().toString(), service);
  }

  public static void delete(String id) {
    delete(id, false, false);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete) {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    getClient().databaseServices().delete(id, params);
  }

  // Async operations
  public static CompletableFuture<org.umetadata.schema.entity.services.DatabaseService>
      createAsync(CreateDatabaseService request) {
    return CompletableFuture.supplyAsync(() -> create(request));
  }

  public static CompletableFuture<org.umetadata.schema.entity.services.DatabaseService>
      retrieveAsync(String id) {
    return CompletableFuture.supplyAsync(() -> retrieve(id));
  }

  public static CompletableFuture<Void> deleteAsync(
      String id, boolean recursive, boolean hardDelete) {
    return CompletableFuture.runAsync(() -> delete(id, recursive, hardDelete));
  }
}
