package org.umetadata.service.resources.services.drive;

import org.umetadata.schema.api.services.CreateDriveService;
import org.umetadata.schema.entity.services.DriveService;
import org.umetadata.service.mapper.EntityMapper;

public class DriveServiceMapper implements EntityMapper<DriveService, CreateDriveService> {
  @Override
  public DriveService createToEntity(CreateDriveService create, String user) {
    return copy(new DriveService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
