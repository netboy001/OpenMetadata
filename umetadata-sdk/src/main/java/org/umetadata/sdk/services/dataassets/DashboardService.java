package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateDashboard;
import org.umetadata.schema.entity.data.Dashboard;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DashboardService extends EntityServiceBase<Dashboard> {
  public DashboardService(HttpClient httpClient) {
    super(httpClient, "/v1/dashboards");
  }

  @Override
  protected Class<Dashboard> getEntityClass() {
    return Dashboard.class;
  }

  // Create using CreateDashboard request
  public Dashboard create(CreateDashboard request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Dashboard.class);
  }
}
