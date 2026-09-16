package org.umetadata.sdk.services.kpi;

import org.umetadata.schema.api.dataInsight.kpi.CreateKpiRequest;
import org.umetadata.schema.dataInsight.kpi.Kpi;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class KpiService extends EntityServiceBase<Kpi> {
  public KpiService(HttpClient httpClient) {
    super(httpClient, "/v1/kpi");
  }

  @Override
  protected Class<Kpi> getEntityClass() {
    return Kpi.class;
  }

  public Kpi create(CreateKpiRequest request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Kpi.class);
  }

  // Use base class update method which does proper JSON Patch
}
