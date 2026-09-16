package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

/**
 * SDK wrapper for TestCase operations.
 * This class provides static methods for TestCase CRUD operations.
 * It does NOT extend the schema TestCase class to avoid naming conflicts.
 */
public class TestCase {

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
  public static org.umetadata.schema.tests.TestCase create(
      org.umetadata.schema.api.tests.CreateTestCase createTestCase)
      throws UMetadataException {
    return getClient().testCases().create(createTestCase);
  }

  public static org.umetadata.schema.tests.TestCase retrieve(String id)
      throws UMetadataException {
    return getClient().testCases().get(id);
  }

  public static org.umetadata.schema.tests.TestCase retrieve(String id, String fields)
      throws UMetadataException {
    return getClient().testCases().get(id, fields);
  }

  public static org.umetadata.schema.tests.TestCase retrieve(UUID id)
      throws UMetadataException {
    return retrieve(id.toString());
  }

  public static org.umetadata.schema.tests.TestCase retrieveByName(String name)
      throws UMetadataException {
    return getClient().testCases().getByName(name);
  }

  public static org.umetadata.schema.tests.TestCase retrieveByName(String name, String fields)
      throws UMetadataException {
    return getClient().testCases().getByName(name, fields);
  }

  public static org.umetadata.schema.tests.TestCase update(
      String id, org.umetadata.schema.tests.TestCase patch) throws UMetadataException {
    return getClient().testCases().update(id, patch);
  }

  public static void delete(String id) throws UMetadataException {
    getClient().testCases().delete(id);
  }

  public static void delete(UUID id) throws UMetadataException {
    getClient().testCases().delete(id);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    getClient().testCases().delete(id, params);
  }

  // Async delete methods
  public static CompletableFuture<Void> deleteAsync(String id) {
    return getClient().testCases().deleteAsync(id);
  }

  public static CompletableFuture<Void> deleteAsync(UUID id) {
    return getClient().testCases().deleteAsync(id);
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

  public static ListResponse<org.umetadata.schema.tests.TestCase> list()
      throws UMetadataException {
    return getClient().testCases().list();
  }

  public static ListResponse<org.umetadata.schema.tests.TestCase> list(ListParams params)
      throws UMetadataException {
    return getClient().testCases().list(params);
  }
}
