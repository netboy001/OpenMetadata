package org.umetadata.sdk.services.policies;

import org.umetadata.schema.api.policies.CreatePolicy;
import org.umetadata.schema.entity.policies.Policy;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class PolicyService extends EntityServiceBase<Policy> {
  public PolicyService(HttpClient httpClient) {
    super(httpClient, "/v1/policies");
  }

  @Override
  protected Class<Policy> getEntityClass() {
    return Policy.class;
  }

  public Policy create(CreatePolicy request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Policy.class);
  }
}
