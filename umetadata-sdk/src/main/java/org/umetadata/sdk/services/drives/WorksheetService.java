package org.umetadata.sdk.services.drives;

import org.umetadata.schema.api.data.CreateWorksheet;
import org.umetadata.schema.entity.data.Worksheet;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class WorksheetService extends EntityServiceBase<Worksheet> {
  public WorksheetService(HttpClient httpClient) {
    super(httpClient, "/v1/drives/worksheets");
  }

  @Override
  protected Class<Worksheet> getEntityClass() {
    return Worksheet.class;
  }

  public Worksheet create(CreateWorksheet request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Worksheet.class);
  }
}
