package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateMetadataService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class MetadataServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.MetadataService> {

  public MetadataServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/metadataServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.MetadataService> getEntityClass() {
    return org.umetadata.schema.entity.services.MetadataService.class;
  }

  public org.umetadata.schema.entity.services.MetadataService create(
      CreateMetadataService request) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.MetadataService.class);
  }
}
