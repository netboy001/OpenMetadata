package org.umetadata.sdk.services.ai;

import org.umetadata.schema.api.ai.CreateLLMModel;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class LLMModelService extends EntityServiceBase<org.umetadata.schema.entity.ai.LLMModel> {

  public LLMModelService(HttpClient httpClient) {
    super(httpClient, "/v1/llmModels");
  }

  @Override
  protected Class<org.umetadata.schema.entity.ai.LLMModel> getEntityClass() {
    return org.umetadata.schema.entity.ai.LLMModel.class;
  }

  public org.umetadata.schema.entity.ai.LLMModel create(CreateLLMModel request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST, basePath, request, org.umetadata.schema.entity.ai.LLMModel.class);
  }
}
