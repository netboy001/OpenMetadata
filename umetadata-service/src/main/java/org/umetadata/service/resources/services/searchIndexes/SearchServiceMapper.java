package org.umetadata.service.resources.services.searchIndexes;

import org.umetadata.schema.api.services.CreateSearchService;
import org.umetadata.schema.entity.services.SearchService;
import org.umetadata.service.mapper.EntityMapper;

public class SearchServiceMapper implements EntityMapper<SearchService, CreateSearchService> {
  @Override
  public SearchService createToEntity(CreateSearchService create, String user) {
    return copy(new SearchService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
