package org.umetadata.sdk.services.classification;

import org.umetadata.schema.api.classification.CreateClassification;
import org.umetadata.schema.entity.classification.Classification;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class ClassificationService extends EntityServiceBase<Classification> {
  public ClassificationService(HttpClient httpClient) {
    super(httpClient, "/v1/classifications");
  }

  @Override
  protected Class<Classification> getEntityClass() {
    return Classification.class;
  }

  // Create classification using CreateClassification request
  public Classification create(CreateClassification request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Classification.class);
  }
}
