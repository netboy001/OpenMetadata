package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class SearchService extends org.umetadata.schema.entity.services.SearchService {

  public static SearchService create(org.umetadata.schema.entity.services.SearchService entity)
      throws UMetadataException {
    return (SearchService) UMetadata.client().searchServices().create(entity);
  }

  public static SearchService retrieve(String id) throws UMetadataException {
    return (SearchService) UMetadata.client().searchServices().get(id);
  }

  public static SearchService retrieve(UUID id) throws UMetadataException {
    return retrieve(id.toString());
  }

  public static SearchService retrieveByName(String name) throws UMetadataException {
    return (SearchService) UMetadata.client().searchServices().getByName(name);
  }

  public static SearchService update(
      String id, org.umetadata.schema.entity.services.SearchService patch)
      throws UMetadataException {
    return (SearchService) UMetadata.client().searchServices().update(id, patch);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete)
      throws UMetadataException {
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    UMetadata.client().searchServices().delete(id, params);
  }

  public static ListResponse<org.umetadata.schema.entity.services.SearchService> list()
      throws UMetadataException {
    return UMetadata.client().searchServices().list();
  }

  public static ListResponse<org.umetadata.schema.entity.services.SearchService> list(
      ListParams params) throws UMetadataException {
    return UMetadata.client().searchServices().list(params);
  }
}
