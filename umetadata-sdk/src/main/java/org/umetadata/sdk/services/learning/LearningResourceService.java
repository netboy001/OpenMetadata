package org.umetadata.sdk.services.learning;

import org.umetadata.schema.api.learning.CreateLearningResource;
import org.umetadata.schema.entity.learning.LearningResource;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class LearningResourceService extends EntityServiceBase<LearningResource> {
  public LearningResourceService(HttpClient httpClient) {
    super(httpClient, "/v1/learning/resources");
  }

  @Override
  protected Class<LearningResource> getEntityClass() {
    return LearningResource.class;
  }

  public LearningResource create(CreateLearningResource request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, LearningResource.class);
  }

  public LearningResource put(CreateLearningResource request) throws UMetadataException {
    return httpClient.execute(HttpMethod.PUT, basePath, request, LearningResource.class);
  }
}
