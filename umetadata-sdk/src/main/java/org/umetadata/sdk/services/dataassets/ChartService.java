package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateChart;
import org.umetadata.schema.entity.data.Chart;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class ChartService extends EntityServiceBase<Chart> {
  public ChartService(HttpClient httpClient) {
    super(httpClient, "/v1/charts");
  }

  @Override
  protected Class<Chart> getEntityClass() {
    return Chart.class;
  }

  // Create chart using CreateChart request
  public Chart create(CreateChart request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Chart.class);
  }
}
