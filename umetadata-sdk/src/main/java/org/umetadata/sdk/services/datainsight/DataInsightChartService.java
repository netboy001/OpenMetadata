package org.umetadata.sdk.services.datainsight;

import org.umetadata.schema.api.dataInsight.CreateDataInsightChart;
import org.umetadata.schema.dataInsight.DataInsightChart;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DataInsightChartService extends EntityServiceBase<DataInsightChart> {
  public DataInsightChartService(HttpClient httpClient) {
    super(httpClient, "/v1/analytics/dataInsights/charts");
  }

  @Override
  protected Class<DataInsightChart> getEntityClass() {
    return DataInsightChart.class;
  }

  public DataInsightChart create(CreateDataInsightChart request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, DataInsightChart.class);
  }

  // Use base class update method which does proper JSON Patch
}
