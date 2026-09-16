package org.umetadata.service.resources.databases;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateDatabaseSchema;
import org.umetadata.schema.entity.data.DatabaseSchema;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class DatabaseSchemaMapper implements EntityMapper<DatabaseSchema, CreateDatabaseSchema> {
  @Override
  public DatabaseSchema createToEntity(CreateDatabaseSchema create, String user) {
    return copy(new DatabaseSchema(), create, user)
        .withDatabase(getEntityReference(Entity.DATABASE, create.getDatabase()))
        .withSourceUrl(create.getSourceUrl())
        .withRetentionPeriod(create.getRetentionPeriod())
        .withSourceHash(create.getSourceHash());
  }
}
