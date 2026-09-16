package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateDashboardService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DashboardServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.DashboardService> {

  public DashboardServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/dashboardServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.DashboardService> getEntityClass() {
    return org.umetadata.schema.entity.services.DashboardService.class;
  }

  // Create using CreateDashboardService request
  public org.umetadata.schema.entity.services.DashboardService create(
      CreateDashboardService request) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.DashboardService.class);
  }
}
