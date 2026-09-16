package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateMetric;
import org.umetadata.schema.entity.data.Metric;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class MetricService extends EntityServiceBase<Metric> {
  public MetricService(HttpClient httpClient) {
    super(httpClient, "/v1/metrics");
  }

  @Override
  protected Class<Metric> getEntityClass() {
    return Metric.class;
  }

  // Create metric using CreateMetric request
  public Metric create(CreateMetric request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Metric.class);
  }
}
