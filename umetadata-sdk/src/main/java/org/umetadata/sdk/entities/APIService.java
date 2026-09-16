package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class APIService extends org.umetadata.schema.entity.services.ApiService {

  public static APIService create(org.umetadata.schema.entity.services.ApiService entity)
      throws UMetadataException {
    return (APIService) UMetadata.client().apiServices().create(entity);
  }

  public static APIService retrieve(String id) throws UMetadataException {
    return (APIService) UMetadata.client().apiServices().get(id);
  }

  public static APIService retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static APIService retrieveByName(String name) throws UMetadataException {
    return (APIService) UMetadata.client().apiServices().getByName(name);
  }

  public static APIService update(
      String id, org.umetadata.schema.entity.services.ApiService patch)
      throws UMetadataException {
    return (APIService) UMetadata.client().apiServices().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().apiServices().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.services.ApiService> list()
      throws UMetadataException {
    return UMetadata.client().apiServices().list();
  }

  public static ListResponse<org.umetadata.schema.entity.services.ApiService> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().apiServices().list(params);
  }
}
