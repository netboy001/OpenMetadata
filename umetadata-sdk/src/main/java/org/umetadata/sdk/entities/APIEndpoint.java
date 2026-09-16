package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class APIEndpoint extends org.umetadata.schema.entity.data.APIEndpoint {

  public static APIEndpoint create(org.umetadata.schema.entity.data.APIEndpoint entity)
      throws UMetadataException {
    return (APIEndpoint) UMetadata.client().apiEndpoints().create(entity);
  }

  public static APIEndpoint retrieve(String id) throws UMetadataException {
    return (APIEndpoint) UMetadata.client().apiEndpoints().get(id);
  }

  public static APIEndpoint retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static APIEndpoint retrieveByName(String name) throws UMetadataException {
    return (APIEndpoint) UMetadata.client().apiEndpoints().getByName(name);
  }

  public static APIEndpoint update(String id, org.umetadata.schema.entity.data.APIEndpoint patch)
      throws UMetadataException {
    return (APIEndpoint) UMetadata.client().apiEndpoints().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().apiEndpoints().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.data.APIEndpoint> list()
      throws UMetadataException {
    return UMetadata.client().apiEndpoints().list();
  }

  public static ListResponse<org.umetadata.schema.entity.data.APIEndpoint> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().apiEndpoints().list(params);
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
