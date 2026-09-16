package org.umetadata.sdk.services.ai;

import org.umetadata.schema.api.ai.CreateAIApplication;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class AIApplicationService
    extends EntityServiceBase<org.umetadata.schema.entity.ai.AIApplication> {

  public AIApplicationService(HttpClient httpClient) {
    super(httpClient, "/v1/aiApplications");
  }

  @Override
  protected Class<org.umetadata.schema.entity.ai.AIApplication> getEntityClass() {
    return org.umetadata.schema.entity.ai.AIApplication.class;
  }

  public org.umetadata.schema.entity.ai.AIApplication create(CreateAIApplication request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST, basePath, request, org.umetadata.schema.entity.ai.AIApplication.class);
  }
}
