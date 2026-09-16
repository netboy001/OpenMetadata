package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.data.Chart;
import org.umetadata.service.Entity;

public record ChartIndex(Chart chart) implements SearchIndex {
  @Override
  public Object getEntity() {
    return chart;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> esDoc) {
    Map<String, Object> commonAttributes = getCommonAttributesMap(chart, Entity.CHART);
    esDoc.putAll(commonAttributes);
    esDoc.put("upstreamLineage", SearchIndex.getLineageData(chart.getEntityReference()));
    return esDoc;
  }
}
