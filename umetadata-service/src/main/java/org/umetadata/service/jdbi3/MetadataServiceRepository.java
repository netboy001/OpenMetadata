package org.umetadata.service.jdbi3;

import org.umetadata.schema.entity.services.MetadataConnection;
import org.umetadata.schema.entity.services.MetadataService;
import org.umetadata.schema.entity.services.ServiceType;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.services.database.DatabaseServiceResource;

public class MetadataServiceRepository
    extends ServiceEntityRepository<MetadataService, MetadataConnection> {
  private static final String UPDATE_FIELDS = "owners,tags,connection";

  public MetadataServiceRepository() {
    super(
        DatabaseServiceResource.COLLECTION_PATH,
        Entity.METADATA_SERVICE,
        Entity.getCollectionDAO().metadataServiceDAO(),
        MetadataConnection.class,
        UPDATE_FIELDS,
        ServiceType.METADATA);
    supportsSearch = true;
  }
}
