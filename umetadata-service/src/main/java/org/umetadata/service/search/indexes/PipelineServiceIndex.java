package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.services.PipelineService;
import org.umetadata.service.Entity;

public record PipelineServiceIndex(PipelineService pipelineService) implements SearchIndex {
  @Override
  public Object getEntity() {
    return pipelineService;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes =
        getCommonAttributesMap(pipelineService, Entity.PIPELINE_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(pipelineService.getEntityReference()));
    return doc;
  }
}
