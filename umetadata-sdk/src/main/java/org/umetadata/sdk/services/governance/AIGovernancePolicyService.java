package org.umetadata.sdk.services.governance;

import org.umetadata.schema.api.ai.CreateAIGovernancePolicy;
import org.umetadata.schema.entity.ai.AIGovernancePolicy;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class AIGovernancePolicyService extends EntityServiceBase<AIGovernancePolicy> {
  public AIGovernancePolicyService(HttpClient httpClient) {
    super(httpClient, "/v1/aiGovernancePolicies");
  }

  @Override
  protected Class<AIGovernancePolicy> getEntityClass() {
    return AIGovernancePolicy.class;
  }

  public AIGovernancePolicy create(CreateAIGovernancePolicy request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, AIGovernancePolicy.class);
  }
}
