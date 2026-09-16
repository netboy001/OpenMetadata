package org.umetadata.service.resources.databases;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateStoredProcedure;
import org.umetadata.schema.entity.data.StoredProcedure;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class StoredProcedureMapper implements EntityMapper<StoredProcedure, CreateStoredProcedure> {
  @Override
  public StoredProcedure createToEntity(CreateStoredProcedure create, String user) {
    return copy(new StoredProcedure(), create, user)
        .withDatabaseSchema(getEntityReference(Entity.DATABASE_SCHEMA, create.getDatabaseSchema()))
        .withStoredProcedureCode(create.getStoredProcedureCode())
        .withStoredProcedureType(create.getStoredProcedureType())
        .withSourceUrl(create.getSourceUrl())
        .withSourceHash(create.getSourceHash());
  }
}
