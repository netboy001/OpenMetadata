package org.umetadata.service.resources.apis;

import static org.umetadata.service.util.EntityUtil.getEntityReference;
import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import org.umetadata.schema.api.data.CreateAPICollection;
import org.umetadata.schema.entity.data.APICollection;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class APICollectionMapper implements EntityMapper<APICollection, CreateAPICollection> {
  @Override
  public APICollection createToEntity(CreateAPICollection create, String user) {
    return copy(new APICollection(), create, user)
        .withService(getEntityReference(Entity.API_SERVICE, create.getService()))
        .withEndpointURL(create.getEndpointURL())
        .withApiEndpoints(getEntityReferences(Entity.API_ENDPOINT, create.getApiEndpoints()))
        .withSourceHash(create.getSourceHash());
  }
}
