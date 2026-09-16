package org.umetadata.sdk.services.events;

import org.umetadata.schema.api.events.CreateEventSubscription;
import org.umetadata.schema.entity.events.EventSubscription;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class EventSubscriptionService extends EntityServiceBase<EventSubscription> {

  public EventSubscriptionService(HttpClient httpClient) {
    super(httpClient, "/v1/events/subscriptions");
  }

  @Override
  protected Class<EventSubscription> getEntityClass() {
    return EventSubscription.class;
  }

  public EventSubscription create(CreateEventSubscription request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, EventSubscription.class);
  }
}
