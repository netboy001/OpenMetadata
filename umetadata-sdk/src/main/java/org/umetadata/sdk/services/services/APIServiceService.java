package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateApiService;
import org.umetadata.schema.entity.services.ApiService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class APIServiceService extends EntityServiceBase<ApiService> {

  public APIServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/apiServices");
  }

  @Override
  protected Class<ApiService> getEntityClass() {
    return ApiService.class;
  }

  public ApiService create(CreateApiService request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, ApiService.class);
  }
}
