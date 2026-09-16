package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class TestDefinition extends org.umetadata.schema.tests.TestDefinition {

  public static TestDefinition create(org.umetadata.schema.tests.TestDefinition entity)
      throws UMetadataException {
    return (TestDefinition) UMetadata.client().testDefinitions().create(entity);
  }

  public static TestDefinition retrieve(String id) throws UMetadataException {
    return (TestDefinition) UMetadata.client().testDefinitions().get(id);
  }

  public static TestDefinition retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static TestDefinition retrieveByName(String name) throws UMetadataException {
    return (TestDefinition) UMetadata.client().testDefinitions().getByName(name);
  }

  public static TestDefinition update(String id, org.umetadata.schema.tests.TestDefinition patch)
      throws UMetadataException {
    return (TestDefinition) UMetadata.client().testDefinitions().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().testDefinitions().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.tests.TestDefinition> list()
      throws UMetadataException {
    return UMetadata.client().testDefinitions().list();
  }

  public static ListResponse<org.umetadata.schema.tests.TestDefinition> list(ListParams params)
      throws UMetadataException {
    return UMetadata.client().testDefinitions().list(params);
  }

  // Add async delete for all entities extending EntityServiceBase
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
}
