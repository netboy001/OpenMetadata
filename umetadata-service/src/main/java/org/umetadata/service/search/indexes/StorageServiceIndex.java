package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.services.StorageService;
import org.umetadata.service.Entity;

public record StorageServiceIndex(StorageService storageService) implements SearchIndex {

  @Override
  public Object getEntity() {
    return storageService;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes =
        getCommonAttributesMap(storageService, Entity.STORAGE_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(storageService.getEntityReference()));
    return doc;
  }
}
