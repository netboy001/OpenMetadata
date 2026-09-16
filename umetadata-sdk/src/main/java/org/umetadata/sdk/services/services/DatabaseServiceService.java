package org.umetadata.sdk.services.services;

import java.util.UUID;
import org.umetadata.schema.api.services.CreateDatabaseService;
import org.umetadata.schema.entity.services.connections.TestConnectionResult;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DatabaseServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.DatabaseService> {

  public DatabaseServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/databaseServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.DatabaseService> getEntityClass() {
    return org.umetadata.schema.entity.services.DatabaseService.class;
  }

  // Create using CreateDatabaseService request
  public org.umetadata.schema.entity.services.DatabaseService create(
      CreateDatabaseService request) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.DatabaseService.class);
  }

  /**
   * Add test connection result to a database service.
   *
   * @param id Service ID
   * @param testConnectionResult Test connection result to add
   * @return Updated database service
   * @throws UMetadataException if request fails
   */
  public org.umetadata.schema.entity.services.DatabaseService addTestConnectionResult(
      UUID id, TestConnectionResult testConnectionResult) throws UMetadataException {
    return addTestConnectionResult(id.toString(), testConnectionResult);
  }

  /**
   * Add test connection result to a database service.
   *
   * @param id Service ID as string
   * @param testConnectionResult Test connection result to add
   * @return Updated database service
   * @throws UMetadataException if request fails
   */
  public org.umetadata.schema.entity.services.DatabaseService addTestConnectionResult(
      String id, TestConnectionResult testConnectionResult) throws UMetadataException {
    String path = basePath + "/" + id + "/testConnectionResult";
    return httpClient.execute(
        HttpMethod.PUT,
        path,
        testConnectionResult,
        org.umetadata.schema.entity.services.DatabaseService.class);
  }
}
