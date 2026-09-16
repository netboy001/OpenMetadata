package org.umetadata.service.resources.charts;

import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import org.umetadata.schema.api.data.CreateChart;
import org.umetadata.schema.entity.data.Chart;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.EntityUtil;

public class ChartMapper implements EntityMapper<Chart, CreateChart> {
  @Override
  public Chart createToEntity(CreateChart create, String user) {
    return copy(new Chart(), create, user)
        .withService(EntityUtil.getEntityReference(Entity.DASHBOARD_SERVICE, create.getService()))
        .withChartType(create.getChartType())
        .withSourceUrl(create.getSourceUrl())
        .withSourceHash(create.getSourceHash())
        .withDashboards(getEntityReferences(Entity.DASHBOARD, create.getDashboards()));
  }
}
