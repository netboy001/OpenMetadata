package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.services.MlModelService;
import org.umetadata.service.Entity;

public record MlModelServiceIndex(MlModelService mlModelService) implements SearchIndex {

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes =
        getCommonAttributesMap(mlModelService, Entity.MLMODEL_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(mlModelService.getEntityReference()));
    return doc;
  }

  @Override
  public Object getEntity() {
    return mlModelService;
  }
}
