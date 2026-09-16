package org.umetadata.service.resources.events;

import org.umetadata.schema.api.events.CreateNotificationTemplate;
import org.umetadata.schema.entity.events.NotificationTemplate;
import org.umetadata.schema.type.ProviderType;
import org.umetadata.service.mapper.EntityMapper;

public class NotificationTemplateMapper
    implements EntityMapper<NotificationTemplate, CreateNotificationTemplate> {

  @Override
  public NotificationTemplate createToEntity(CreateNotificationTemplate create, String user) {
    NotificationTemplate notificationTemplate =
        copy(new NotificationTemplate(), create, user)
            .withTemplateSubject(create.getTemplateSubject())
            .withTemplateBody(create.getTemplateBody());

    if (notificationTemplate != null) {
      notificationTemplate.withProvider(ProviderType.USER);
    }

    return notificationTemplate;
  }
}
