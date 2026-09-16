package org.umetadata.sdk.services.dataassets;

import java.util.UUID;
import org.umetadata.schema.api.data.CreateQuery;
import org.umetadata.schema.entity.data.Query;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class QueryService extends EntityServiceBase<Query> {
  public QueryService(HttpClient httpClient) {
    super(httpClient, "/v1/queries");
  }

  @Override
  protected Class<Query> getEntityClass() {
    return Query.class;
  }

  // Create query using CreateQuery request
  public Query create(CreateQuery request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Query.class);
  }

  // Update/upsert query using CreateQuery request
  public Query update(UUID id, CreateQuery request) throws UMetadataException {
    return httpClient.execute(HttpMethod.PUT, basePath, request, Query.class);
  }

  public Query update(String id, CreateQuery request) throws UMetadataException {
    return httpClient.execute(HttpMethod.PUT, basePath, request, Query.class);
  }
}
