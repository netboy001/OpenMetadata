package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateLLMService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class LLMServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.LLMService> {

  public LLMServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/llmServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.LLMService> getEntityClass() {
    return org.umetadata.schema.entity.services.LLMService.class;
  }

  public org.umetadata.schema.entity.services.LLMService create(CreateLLMService request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.LLMService.class);
  }
}
