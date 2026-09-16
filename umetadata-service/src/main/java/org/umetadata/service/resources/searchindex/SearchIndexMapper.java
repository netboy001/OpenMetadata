package org.umetadata.service.resources.searchindex;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateSearchIndex;
import org.umetadata.schema.entity.data.SearchIndex;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class SearchIndexMapper implements EntityMapper<SearchIndex, CreateSearchIndex> {
  @Override
  public SearchIndex createToEntity(CreateSearchIndex create, String user) {
    return copy(new SearchIndex(), create, user)
        .withService(getEntityReference(Entity.SEARCH_SERVICE, create.getService()))
        .withFields(create.getFields())
        .withSearchIndexSettings(create.getSearchIndexSettings())
        .withSourceHash(create.getSourceHash())
        .withIndexType(create.getIndexType());
  }
}
