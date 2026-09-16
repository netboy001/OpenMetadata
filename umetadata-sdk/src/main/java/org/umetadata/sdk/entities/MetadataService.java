package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class MetadataService extends org.umetadata.schema.entity.services.MetadataService {

  public static MetadataService create(
      org.umetadata.schema.entity.services.MetadataService entity) throws UMetadataException {
    return (MetadataService) UMetadata.client().metadataServices().create(entity);
  }

  public static MetadataService retrieve(String id) throws UMetadataException {
    return (MetadataService) UMetadata.client().metadataServices().get(id);
  }

  public static MetadataService retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static MetadataService retrieveByName(String name) throws UMetadataException {
    return (MetadataService) UMetadata.client().metadataServices().getByName(name);
  }

  public static MetadataService update(
      String id, org.umetadata.schema.entity.services.MetadataService patch)
      throws UMetadataException {
    return (MetadataService) UMetadata.client().metadataServices().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().metadataServices().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.services.MetadataService> list()
      throws UMetadataException {
    return UMetadata.client().metadataServices().list();
  }

  public static ListResponse<org.umetadata.schema.entity.services.MetadataService> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().metadataServices().list(params);
  }
}
