package org.umetadata.sdk.services.services;

import org.umetadata.schema.api.services.CreateMessagingService;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class MessagingServiceService
    extends EntityServiceBase<org.umetadata.schema.entity.services.MessagingService> {

  public MessagingServiceService(HttpClient httpClient) {
    super(httpClient, "/v1/services/messagingServices");
  }

  @Override
  protected Class<org.umetadata.schema.entity.services.MessagingService> getEntityClass() {
    return org.umetadata.schema.entity.services.MessagingService.class;
  }

  // Create using CreateMessagingService request
  public org.umetadata.schema.entity.services.MessagingService create(
      CreateMessagingService request) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST,
        basePath,
        request,
        org.umetadata.schema.entity.services.MessagingService.class);
  }
}
