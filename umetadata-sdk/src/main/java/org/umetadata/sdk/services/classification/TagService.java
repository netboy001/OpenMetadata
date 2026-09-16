package org.umetadata.sdk.services.classification;

import org.umetadata.schema.api.classification.CreateTag;
import org.umetadata.schema.entity.classification.Tag;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class TagService extends EntityServiceBase<Tag> {
  public TagService(HttpClient httpClient) {
    super(httpClient, "/v1/tags");
  }

  @Override
  protected Class<Tag> getEntityClass() {
    return Tag.class;
  }

  // Create tag using CreateTag request
  public Tag create(CreateTag request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Tag.class);
  }
}
