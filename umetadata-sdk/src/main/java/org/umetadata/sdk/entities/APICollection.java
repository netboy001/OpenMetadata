package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class APICollection extends org.umetadata.schema.entity.data.APICollection {

  public static APICollection create(org.umetadata.schema.entity.data.APICollection entity)
      throws UMetadataException {
    return (APICollection) UMetadata.client().apiCollections().create(entity);
  }

  public static APICollection retrieve(String id) throws UMetadataException {
    return (APICollection) UMetadata.client().apiCollections().get(id);
  }

  public static APICollection retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static APICollection retrieveByName(String name) throws UMetadataException {
    return (APICollection) UMetadata.client().apiCollections().getByName(name);
  }

  public static APICollection update(
      String id, org.umetadata.schema.entity.data.APICollection patch)
      throws UMetadataException {
    return (APICollection) UMetadata.client().apiCollections().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().apiCollections().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.data.APICollection> list()
      throws UMetadataException {
    return UMetadata.client().apiCollections().list();
  }

  public static ListResponse<org.umetadata.schema.entity.data.APICollection> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().apiCollections().list(params);
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
