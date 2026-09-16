package org.umetadata.sdk.services.storages;

import org.umetadata.schema.api.data.CreateSpreadsheet;
import org.umetadata.schema.entity.data.Spreadsheet;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class SpreadsheetService extends EntityServiceBase<Spreadsheet> {
  public SpreadsheetService(HttpClient httpClient) {
    super(httpClient, "/v1/drives/spreadsheets");
  }

  @Override
  protected Class<Spreadsheet> getEntityClass() {
    return Spreadsheet.class;
  }

  public Spreadsheet create(CreateSpreadsheet request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Spreadsheet.class);
  }
}
