package org.umetadata.service.resources.services.storage;

import org.umetadata.schema.api.services.CreateStorageService;
import org.umetadata.schema.entity.services.StorageService;
import org.umetadata.service.mapper.EntityMapper;

public class StorageServiceMapper implements EntityMapper<StorageService, CreateStorageService> {
  @Override
  public StorageService createToEntity(CreateStorageService create, String user) {
    return copy(new StorageService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
