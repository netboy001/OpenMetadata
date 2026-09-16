package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateMlModelService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class MlModelServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.MlModelService> {

  public MlModelServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/mlmodelServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.MlModelService> getEntityClass() {
    return org.umetadata.schema.entity.services.MlModelService.class;
  }

  // Create using CreateMlModelService request
  public org.umetadata.schema.entity.services.MlModelService create(CreateMlModelService request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.MlModelService.class);
  }
}
