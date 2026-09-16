package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class IngestionPipeline
    extends org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline {

  public static IngestionPipeline create(
      org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline entity)
      throws UMetadataException {
    return (IngestionPipeline) UMetadata.client().ingestionPipelines().create(entity);
  }

  public static IngestionPipeline retrieve(String id) throws UMetadataException {
    return (IngestionPipeline) UMetadata.client().ingestionPipelines().get(id);
  }

  public static IngestionPipeline retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static IngestionPipeline retrieveByName(String name) throws UMetadataException {
    return (IngestionPipeline) UMetadata.client().ingestionPipelines().getByName(name);
  }

  public static IngestionPipeline update(
      String id, org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline patch)
      throws UMetadataException {
    return (IngestionPipeline) UMetadata.client().ingestionPipelines().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().ingestionPipelines().delete(id, params);
  }

  public static ListResponse<
          org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline>
      list() throws UMetadataException {
    return UMetadata.client().ingestionPipelines().list();
  }

  public static ListResponse<
          org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline>
      list(ListParams params) throws UMetadataException {
    return UMetadata.client().ingestionPipelines().list(params);
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
