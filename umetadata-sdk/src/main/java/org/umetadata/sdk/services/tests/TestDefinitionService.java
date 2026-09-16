package org.umetadata.sdk.services.tests;

import org.umetadata.schema.api.tests.CreateTestDefinition;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class TestDefinitionService
    extends EntityServiceBase<org.umetadata.schema.tests.TestDefinition> {

  public TestDefinitionService(HttpClient httpClient) {
    super(httpClient, "/v1/dataQuality/testDefinitions");
  }

  @Override
  protected Class<org.umetadata.schema.tests.TestDefinition> getEntityClass() {
    return org.umetadata.schema.tests.TestDefinition.class;
  }

  // Create using CreateTestDefinition request
  public org.umetadata.schema.tests.TestDefinition create(CreateTestDefinition request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST, basePath, request, org.umetadata.schema.tests.TestDefinition.class);
  }
}
