package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateSearchIndex;
import org.umetadata.schema.entity.data.SearchIndex;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class SearchIndexService extends EntityServiceBase<SearchIndex> {
  public SearchIndexService(HttpClient httpClient) {
    super(httpClient, "/v1/searchIndexes");
  }

  @Override
  protected Class<SearchIndex> getEntityClass() {
    return SearchIndex.class;
  }

  // Create searchindex using CreateSearchIndex request
  public SearchIndex create(CreateSearchIndex request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, SearchIndex.class);
  }
}
