package org.umetadata.service.jdbi3;

import org.umetadata.schema.entity.services.SearchService;
import org.umetadata.schema.entity.services.ServiceType;
import org.umetadata.schema.type.SearchConnection;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.services.storage.StorageServiceResource;

public class SearchServiceRepository
    extends ServiceEntityRepository<SearchService, SearchConnection> {
  public SearchServiceRepository() {
    super(
        StorageServiceResource.COLLECTION_PATH,
        Entity.SEARCH_SERVICE,
        Entity.getCollectionDAO().searchServiceDAO(),
        SearchConnection.class,
        "",
        ServiceType.SEARCH);
    supportsSearch = true;
  }
}
