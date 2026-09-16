package org.umetadata.service.resources.entityProfiles;

import org.umetadata.schema.api.data.CreateEntityProfile;
import org.umetadata.schema.type.EntityProfile;
import org.umetadata.service.mapper.EntityTimeSeriesMapper;

public class EntityProfileMapper
    implements EntityTimeSeriesMapper<EntityProfile, CreateEntityProfile> {
  @Override
  public EntityProfile createToEntity(CreateEntityProfile create, String user) {
    return copy(EntityProfile.class)
        .withTimestamp(create.getTimestamp())
        .withProfileData(create.getProfileData())
        .withProfileType(create.getProfileType());
  }
}
