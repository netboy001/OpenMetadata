package org.umetadata.sdk.services.ai;

import org.umetadata.schema.api.ai.CreatePromptTemplate;
import org.umetadata.schema.entity.ai.PromptTemplate;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class PromptTemplateService extends EntityServiceBase<PromptTemplate> {
  public PromptTemplateService(HttpClient httpClient) {
    super(httpClient, "/v1/promptTemplates");
  }

  @Override
  protected Class<PromptTemplate> getEntityClass() {
    return PromptTemplate.class;
  }

  public PromptTemplate create(CreatePromptTemplate request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, PromptTemplate.class);
  }
}
