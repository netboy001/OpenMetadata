package org.umetadata.service.resources.services.messaging;

import org.umetadata.schema.api.services.CreateMessagingService;
import org.umetadata.schema.entity.services.MessagingService;
import org.umetadata.service.mapper.EntityMapper;

public class MessagingServiceMapper
    implements EntityMapper<MessagingService, CreateMessagingService> {
  @Override
  public MessagingService createToEntity(CreateMessagingService create, String user) {
    return copy(new MessagingService(), create, user)
        .withConnection(create.getConnection())
        .withServiceType(create.getServiceType())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
