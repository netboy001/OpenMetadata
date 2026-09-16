package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.services.MetadataService;
import org.umetadata.service.Entity;

public record MetadataServiceIndex(MetadataService metadataService) implements SearchIndex {

  @Override
  public Object getEntity() {
    return metadataService;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes =
        getCommonAttributesMap(metadataService, Entity.METADATA_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(metadataService.getEntityReference()));
    return doc;
  }
}
