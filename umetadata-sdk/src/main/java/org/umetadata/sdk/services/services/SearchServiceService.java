package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateSearchService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class SearchServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.SearchService> {

  public SearchServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/searchServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.SearchService> getEntityClass() {
    return org.umetadata.schema.entity.services.SearchService.class;
  }

  // Create using CreateSearchService request
  public org.umetadata.schema.entity.services.SearchService create(CreateSearchService request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.SearchService.class);
  }
}
