package org.umetadata.service.jdbi3;

import org.umetadata.schema.entity.services.ServiceType;
import org.umetadata.schema.entity.services.StorageService;
import org.umetadata.schema.type.StorageConnection;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.services.storage.StorageServiceResource;

public class StorageServiceRepository
    extends ServiceEntityRepository<StorageService, StorageConnection> {
  public StorageServiceRepository() {
    super(
        StorageServiceResource.COLLECTION_PATH,
        Entity.STORAGE_SERVICE,
        Entity.getCollectionDAO().storageServiceDAO(),
        StorageConnection.class,
        "",
        ServiceType.STORAGE);
    supportsSearch = true;
  }
}
