package org.umetadata.sdk.services.ingestion;

import org.umetadata.schema.api.services.ingestionPipelines.CreateIngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class IngestionPipelineService extends EntityServiceBase<IngestionPipeline> {

  public IngestionPipelineService(HttpClient httpClient) {
    super(httpClient, "/v1/services/ingestionPipelines");
  }

  @Override
  protected Class<IngestionPipeline> getEntityClass() {
    return IngestionPipeline.class;
  }

  public IngestionPipeline create(CreateIngestionPipeline request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, IngestionPipeline.class);
  }
}
