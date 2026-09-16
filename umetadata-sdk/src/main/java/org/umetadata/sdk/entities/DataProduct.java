package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class DataProduct extends org.umetadata.schema.entity.domains.DataProduct {

  public static DataProduct create(org.umetadata.schema.entity.domains.DataProduct entity)
      throws UMetadataException {
    return (DataProduct) UMetadata.client().dataProducts().create(entity);
  }

  public static DataProduct retrieve(String id) throws UMetadataException {
    return (DataProduct) UMetadata.client().dataProducts().get(id);
  }

  public static DataProduct retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static DataProduct retrieveByName(String name) throws UMetadataException {
    return (DataProduct) UMetadata.client().dataProducts().getByName(name);
  }

  public static DataProduct update(
      String id, org.umetadata.schema.entity.domains.DataProduct patch)
      throws UMetadataException {
    return (DataProduct) UMetadata.client().dataProducts().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().dataProducts().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.domains.DataProduct> list()
      throws UMetadataException {
    return UMetadata.client().dataProducts().list();
  }

  public static ListResponse<org.umetadata.schema.entity.domains.DataProduct> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().dataProducts().list(params);
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
