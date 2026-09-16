package org.umetadata.sdk.services.events;

import org.umetadata.schema.api.events.CreateNotificationTemplate;
import org.umetadata.schema.entity.events.NotificationTemplate;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class NotificationTemplateService extends EntityServiceBase<NotificationTemplate> {

  public NotificationTemplateService(HttpClient httpClient) {
    super(httpClient, "/v1/notificationTemplates");
  }

  @Override
  protected Class<NotificationTemplate> getEntityClass() {
    return NotificationTemplate.class;
  }

  public NotificationTemplate create(CreateNotificationTemplate request)
      throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, NotificationTemplate.class);
  }

  public NotificationTemplate createOrUpdate(CreateNotificationTemplate request)
      throws UMetadataException {
    return httpClient.execute(HttpMethod.PUT, basePath, request, NotificationTemplate.class);
  }
}
