package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateDashboardDataModel;
import org.umetadata.schema.entity.data.DashboardDataModel;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DashboardDataModelService extends EntityServiceBase<DashboardDataModel> {
  public DashboardDataModelService(HttpClient httpClient) {
    super(httpClient, "/v1/dashboard/datamodels");
  }

  @Override
  protected Class<DashboardDataModel> getEntityClass() {
    return DashboardDataModel.class;
  }

  // Create dashboarddatamodel using CreateDashboardDataModel request
  public DashboardDataModel create(CreateDashboardDataModel request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, DashboardDataModel.class);
  }
}
