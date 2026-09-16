package org.umetadata.service.search.indexes;

import java.util.Map;
import java.util.Set;
import org.umetadata.schema.entity.services.LLMService;
import org.umetadata.service.Entity;

public record LlmServiceIndex(LLMService llmService) implements SearchIndex {

  @Override
  public Object getEntity() {
    return llmService;
  }

  @Override
  public Set<String> getExcludedFields() {
    return Set.of("models");
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes = getCommonAttributesMap(llmService, Entity.LLM_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(llmService.getEntityReference()));
    return doc;
  }
}
