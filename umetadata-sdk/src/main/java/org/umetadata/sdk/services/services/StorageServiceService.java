package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateStorageService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class StorageServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.StorageService> {

  public StorageServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/storageServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.StorageService> getEntityClass() {
    return org.umetadata.schema.entity.services.StorageService.class;
  }

  // Create using CreateStorageService request
  public org.umetadata.schema.entity.services.StorageService create(CreateStorageService request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.StorageService.class);
  }
}
