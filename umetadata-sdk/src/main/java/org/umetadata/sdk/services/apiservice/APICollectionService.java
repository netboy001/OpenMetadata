package org.umetadata.sdk.services.apiservice;

import org.umetadata.schema.api.data.CreateAPICollection;
import org.umetadata.schema.entity.data.APICollection;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class APICollectionService extends EntityServiceBase<APICollection> {

  public APICollectionService(HttpClient httpClient) {
    super(httpClient, "/v1/apiCollections");
  }

  @Override
  protected Class<APICollection> getEntityClass() {
    return APICollection.class;
  }

  public APICollection create(CreateAPICollection request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, APICollection.class);
  }
}
