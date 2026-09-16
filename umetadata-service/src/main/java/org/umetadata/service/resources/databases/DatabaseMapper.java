package org.umetadata.service.resources.databases;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateDatabase;
import org.umetadata.schema.entity.data.Database;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class DatabaseMapper implements EntityMapper<Database, CreateDatabase> {
  @Override
  public Database createToEntity(CreateDatabase create, String user) {
    return copy(new Database(), create, user)
        .withService(getEntityReference(Entity.DATABASE_SERVICE, create.getService()))
        .withSourceUrl(create.getSourceUrl())
        .withRetentionPeriod(create.getRetentionPeriod())
        .withSourceHash(create.getSourceHash());
  }
}
