package org.umetadata.service.resources.services.database;

import org.umetadata.schema.api.services.CreateDatabaseService;
import org.umetadata.schema.entity.services.DatabaseService;
import org.umetadata.service.mapper.EntityMapper;

public class DatabaseServiceMapper implements EntityMapper<DatabaseService, CreateDatabaseService> {
  @Override
  public DatabaseService createToEntity(CreateDatabaseService create, String user) {
    return copy(new DatabaseService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
