package org.umetadata.sdk.services.tests;

import org.umetadata.schema.api.tests.CreateTestSuite;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class TestSuiteService extends EntityServiceBase<org.umetadata.schema.tests.TestSuite> {

  public TestSuiteService(HttpClient httpClient) {
    super(httpClient, "/v1/dataQuality/testSuites");
  }

  @Override
  protected Class<org.umetadata.schema.tests.TestSuite> getEntityClass() {
    return org.umetadata.schema.tests.TestSuite.class;
  }

  // Create using CreateTestSuite request
  public org.umetadata.schema.tests.TestSuite create(CreateTestSuite request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST, basePath, request, org.umetadata.schema.tests.TestSuite.class);
  }
}
