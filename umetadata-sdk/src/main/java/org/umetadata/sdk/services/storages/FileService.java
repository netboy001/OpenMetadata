package org.umetadata.sdk.services.storages;

import org.umetadata.schema.api.data.CreateFile;
import org.umetadata.schema.entity.data.File;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class FileService extends EntityServiceBase<File> {
  public FileService(HttpClient httpClient) {
    super(httpClient, "/v1/drives/files");
  }

  @Override
  protected Class<File> getEntityClass() {
    return File.class;
  }

  public File create(CreateFile request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, File.class);
  }
}
