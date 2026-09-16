package org.umetadata.service.resources.analytics;

import org.umetadata.schema.analytics.WebAnalyticEvent;
import org.umetadata.schema.api.tests.CreateWebAnalyticEvent;
import org.umetadata.service.mapper.EntityMapper;

public class WebAnalyticEventMapper
    implements EntityMapper<WebAnalyticEvent, CreateWebAnalyticEvent> {
  @Override
  public WebAnalyticEvent createToEntity(CreateWebAnalyticEvent create, String user) {
    return copy(new WebAnalyticEvent(), create, user)
        .withName(create.getName())
        .withDisplayName(create.getDisplayName())
        .withDescription(create.getDescription())
        .withEventType(create.getEventType());
  }
}
