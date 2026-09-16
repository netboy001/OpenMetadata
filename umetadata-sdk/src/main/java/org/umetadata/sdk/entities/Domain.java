package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.domains.CreateDomain;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class Domain extends org.umetadata.schema.entity.domains.Domain {

  public static Domain create(CreateDomain entity) throws UMetadataException {
    return (Domain) UMetadata.client().domains().create(entity);
  }

  public static Domain retrieve(String id) throws UMetadataException {
    return (Domain) UMetadata.client().domains().get(id);
  }

  public static Domain retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static Domain retrieveByName(String name) throws UMetadataException {
    return (Domain) UMetadata.client().domains().getByName(name);
  }

  public static Domain update(String id, org.umetadata.schema.entity.domains.Domain patch)
      throws UMetadataException {
    return (Domain) UMetadata.client().domains().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().domains().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.domains.Domain> list()
      throws UMetadataException {
    return UMetadata.client().domains().list();
  }

  public static ListResponse<org.umetadata.schema.entity.domains.Domain> list(ListParams params)
      throws UMetadataException {
    return UMetadata.client().domains().list(params);
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
