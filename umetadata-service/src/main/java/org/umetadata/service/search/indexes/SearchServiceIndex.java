package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.services.SearchService;
import org.umetadata.service.Entity;

public record SearchServiceIndex(SearchService searchService) implements SearchIndex {

  @Override
  public Object getEntity() {
    return searchService;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes =
        getCommonAttributesMap(searchService, Entity.SEARCH_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(searchService.getEntityReference()));
    return doc;
  }
}
