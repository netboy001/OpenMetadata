package org.umetadata.sdk.services.apiservice;

import org.umetadata.schema.api.data.CreateAPIEndpoint;
import org.umetadata.schema.entity.data.APIEndpoint;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class APIEndpointService extends EntityServiceBase<APIEndpoint> {

  public APIEndpointService(HttpClient httpClient) {
    super(httpClient, "/v1/apiEndpoints");
  }

  @Override
  protected Class<APIEndpoint> getEntityClass() {
    return APIEndpoint.class;
  }

  public APIEndpoint create(CreateAPIEndpoint request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, APIEndpoint.class);
  }
}
