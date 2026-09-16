package org.umetadata.service.jdbi3;

import org.umetadata.schema.entity.services.ApiService;
import org.umetadata.schema.entity.services.ServiceType;
import org.umetadata.schema.type.ApiConnection;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.services.apiservices.APIServiceResource;

public class APIServiceRepository extends ServiceEntityRepository<ApiService, ApiConnection> {
  public APIServiceRepository() {
    super(
        APIServiceResource.COLLECTION_PATH,
        Entity.API_SERVICE,
        Entity.getCollectionDAO().apiServiceDAO(),
        ApiConnection.class,
        "",
        ServiceType.API);
    supportsSearch = true;
  }
}
