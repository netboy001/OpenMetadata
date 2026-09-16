package org.umetadata.service.resources.apis;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateAPIEndpoint;
import org.umetadata.schema.entity.data.APIEndpoint;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class APIEndpointMapper implements EntityMapper<APIEndpoint, CreateAPIEndpoint> {
  @Override
  public APIEndpoint createToEntity(CreateAPIEndpoint create, String user) {
    return copy(new APIEndpoint(), create, user)
        .withApiCollection(getEntityReference(Entity.API_COLLECTION, create.getApiCollection()))
        .withRequestMethod(create.getRequestMethod())
        .withEndpointURL(create.getEndpointURL())
        .withRequestSchema(create.getRequestSchema())
        .withResponseSchema(create.getResponseSchema())
        .withSourceHash(create.getSourceHash());
  }
}
