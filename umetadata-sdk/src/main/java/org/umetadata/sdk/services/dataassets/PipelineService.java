package org.umetadata.sdk.services.dataassets;

import java.util.List;
import org.umetadata.schema.api.data.CreatePipeline;
import org.umetadata.schema.entity.data.Pipeline;
import org.umetadata.schema.entity.data.PipelineStatus;
import org.umetadata.schema.utils.ResultList;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListResponse;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class PipelineService extends EntityServiceBase<Pipeline> {
  public PipelineService(HttpClient httpClient) {
    super(httpClient, "/v1/pipelines");
  }

  @Override
  protected Class<Pipeline> getEntityClass() {
    return Pipeline.class;
  }

  // Create using CreatePipeline request
  public Pipeline create(CreatePipeline request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Pipeline.class);
  }

  public Pipeline addPipelineStatus(String fqn, PipelineStatus status)
      throws UMetadataException {
    String encodedFqn;
    try {
      encodedFqn = java.net.URLEncoder.encode(fqn, "UTF-8").replace("+", "%20");
    } catch (java.io.UnsupportedEncodingException e) {
      encodedFqn = fqn;
    }
    String path = basePath + "/" + encodedFqn + "/status";
    return httpClient.execute(HttpMethod.PUT, path, status, Pipeline.class);
  }

  public ListResponse<PipelineStatus> listPipelineStatuses(String fqn, Long startTs, Long endTs)
      throws UMetadataException {
    String encodedFqn;
    try {
      encodedFqn = java.net.URLEncoder.encode(fqn, "UTF-8").replace("+", "%20");
    } catch (java.io.UnsupportedEncodingException e) {
      encodedFqn = fqn;
    }
    String path = basePath + "/" + encodedFqn + "/status?startTs=" + startTs + "&endTs=" + endTs;
    ResultList<PipelineStatus> result =
        httpClient.execute(HttpMethod.GET, path, null, getResultListType());
    return new ListResponse<>(result);
  }

  @SuppressWarnings("unchecked")
  private Class<ResultList<PipelineStatus>> getResultListType() {
    return (Class<ResultList<PipelineStatus>>) (Class<?>) ResultList.class;
  }

  public Pipeline addBulkPipelineStatus(String fqn, List<PipelineStatus> statuses)
      throws UMetadataException {
    String encodedFqn;
    try {
      encodedFqn = java.net.URLEncoder.encode(fqn, "UTF-8").replace("+", "%20");
    } catch (java.io.UnsupportedEncodingException e) {
      encodedFqn = fqn;
    }
    String path = basePath + "/" + encodedFqn + "/status/bulk";
    return httpClient.execute(HttpMethod.PUT, path, statuses, Pipeline.class);
  }
}
