package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.service.Entity;

public record APIServiceIndex(org.umetadata.schema.entity.services.ApiService apiService)
    implements SearchIndex {

  @Override
  public Object getEntity() {
    return apiService;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes = getCommonAttributesMap(apiService, Entity.API_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(apiService.getEntityReference()));
    return doc;
  }
}
