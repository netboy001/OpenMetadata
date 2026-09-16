package org.umetadata.sdk.services.databases;

import org.umetadata.schema.api.data.CreateDatabase;
import org.umetadata.schema.entity.data.Database;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DatabaseService extends EntityServiceBase<Database> {
  public DatabaseService(HttpClient httpClient) {
    super(httpClient, "/v1/databases");
  }

  @Override
  protected Class<Database> getEntityClass() {
    return Database.class;
  }

  // Create database using CreateDatabase request
  public Database create(CreateDatabase request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Database.class);
  }
}
