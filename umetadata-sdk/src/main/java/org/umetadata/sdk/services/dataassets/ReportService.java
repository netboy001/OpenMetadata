package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.entity.data.Report;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.services.EntityServiceBase;

public class ReportService extends EntityServiceBase<Report> {
  public ReportService(HttpClient httpClient) {
    super(httpClient, "/v1/reports");
  }

  @Override
  protected Class<Report> getEntityClass() {
    return Report.class;
  }
}
