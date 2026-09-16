package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class TestSuite extends org.umetadata.schema.tests.TestSuite {

  public static TestSuite create(org.umetadata.schema.tests.TestSuite entity)
      throws UMetadataException {
    return (TestSuite) UMetadata.client().testSuites().create(entity);
  }

  public static TestSuite retrieve(String id) throws UMetadataException {
    return (TestSuite) UMetadata.client().testSuites().get(id);
  }

  public static TestSuite retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static TestSuite retrieveByName(String name) throws UMetadataException {
    return (TestSuite) UMetadata.client().testSuites().getByName(name);
  }

  public static TestSuite update(String id, org.umetadata.schema.tests.TestSuite patch)
      throws UMetadataException {
    return (TestSuite) UMetadata.client().testSuites().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().testSuites().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.tests.TestSuite> list()
      throws UMetadataException {
    return UMetadata.client().testSuites().list();
  }

  public static ListResponse<org.umetadata.schema.tests.TestSuite> list(ListParams params)
      throws UMetadataException {
    return UMetadata.client().testSuites().list(params);
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
