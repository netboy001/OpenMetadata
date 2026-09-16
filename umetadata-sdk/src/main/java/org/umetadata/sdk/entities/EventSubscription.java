package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class EventSubscription extends org.umetadata.schema.entity.events.EventSubscription {

  public static EventSubscription create(
      org.umetadata.schema.entity.events.EventSubscription entity) throws UMetadataException {
    return (EventSubscription) UMetadata.client().eventSubscriptions().create(entity);
  }

  public static EventSubscription retrieve(String id) throws UMetadataException {
    return (EventSubscription) UMetadata.client().eventSubscriptions().get(id);
  }

  public static EventSubscription retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static EventSubscription retrieveByName(String name) throws UMetadataException {
    return (EventSubscription) UMetadata.client().eventSubscriptions().getByName(name);
  }

  public static EventSubscription update(
      String id, org.umetadata.schema.entity.events.EventSubscription patch)
      throws UMetadataException {
    return (EventSubscription) UMetadata.client().eventSubscriptions().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().eventSubscriptions().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.events.EventSubscription> list()
      throws UMetadataException {
    return UMetadata.client().eventSubscriptions().list();
  }

  public static ListResponse<org.umetadata.schema.entity.events.EventSubscription> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().eventSubscriptions().list(params);
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
