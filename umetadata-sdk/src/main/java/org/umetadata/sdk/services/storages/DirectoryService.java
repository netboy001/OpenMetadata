package org.umetadata.sdk.services.storages;

import org.umetadata.schema.api.data.CreateDirectory;
import org.umetadata.schema.entity.data.Directory;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DirectoryService extends EntityServiceBase<Directory> {
  public DirectoryService(HttpClient httpClient) {
    super(httpClient, "/v1/drives/directories");
  }

  @Override
  protected Class<Directory> getEntityClass() {
    return Directory.class;
  }

  public Directory create(CreateDirectory request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Directory.class);
  }
}
