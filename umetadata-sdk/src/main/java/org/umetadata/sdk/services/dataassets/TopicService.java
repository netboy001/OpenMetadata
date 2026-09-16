package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateTopic;
import org.umetadata.schema.entity.data.Topic;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class TopicService extends EntityServiceBase<Topic> {
  public TopicService(HttpClient httpClient) {
    super(httpClient, "/v1/topics");
  }

  @Override
  protected Class<Topic> getEntityClass() {
    return Topic.class;
  }

  // Create using CreateTopic request
  public Topic create(CreateTopic request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Topic.class);
  }
}
