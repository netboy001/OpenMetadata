package org.umetadata.sdk.services.databases;

import org.umetadata.schema.api.data.CreateStoredProcedure;
import org.umetadata.schema.entity.data.StoredProcedure;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class StoredProcedureService extends EntityServiceBase<StoredProcedure> {
  public StoredProcedureService(HttpClient httpClient) {
    super(httpClient, "/v1/storedProcedures");
  }

  @Override
  protected Class<StoredProcedure> getEntityClass() {
    return StoredProcedure.class;
  }

  // Create storedprocedure using CreateStoredProcedure request
  public StoredProcedure create(CreateStoredProcedure request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, StoredProcedure.class);
  }
}
