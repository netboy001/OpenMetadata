package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateDriveService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DriveServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.DriveService> {

  public DriveServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/driveServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.DriveService> getEntityClass() {
    return org.umetadata.schema.entity.services.DriveService.class;
  }

  public org.umetadata.schema.entity.services.DriveService create(CreateDriveService request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.DriveService.class);
  }
}
