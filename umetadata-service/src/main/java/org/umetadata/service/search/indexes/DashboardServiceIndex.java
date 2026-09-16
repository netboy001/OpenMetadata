package org.umetadata.service.search.indexes;

import java.util.Map;
import org.umetadata.schema.entity.services.DashboardService;
import org.umetadata.service.Entity;

public record DashboardServiceIndex(DashboardService dashboardService) implements SearchIndex {

  @Override
  public Object getEntity() {
    return dashboardService;
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes =
        getCommonAttributesMap(dashboardService, Entity.DASHBOARD_SERVICE);
    doc.putAll(commonAttributes);
    doc.put("upstreamLineage", SearchIndex.getLineageData(dashboardService.getEntityReference()));
    return doc;
  }
}
