package org.umetadata.sdk.services.bots;

import org.umetadata.schema.api.CreateBot;
import org.umetadata.schema.entity.Bot;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class BotService extends EntityServiceBase<Bot> {
  public BotService(HttpClient httpClient) {
    super(httpClient, "/v1/bots");
  }

  @Override
  protected Class<Bot> getEntityClass() {
    return Bot.class;
  }

  public Bot create(CreateBot request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Bot.class);
  }
}
