package org.umetadata.service.resources.dashboards;

import static org.umetadata.service.util.EntityUtil.getEntityReference;
import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import org.umetadata.schema.api.data.CreateDashboard;
import org.umetadata.schema.entity.data.Dashboard;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class DashboardMapper implements EntityMapper<Dashboard, CreateDashboard> {
  @Override
  public Dashboard createToEntity(CreateDashboard create, String user) {
    return copy(new Dashboard(), create, user)
        .withService(getEntityReference(Entity.DASHBOARD_SERVICE, create.getService()))
        .withCharts(getEntityReferences(Entity.CHART, create.getCharts()))
        .withDataModels(getEntityReferences(Entity.DASHBOARD_DATA_MODEL, create.getDataModels()))
        .withSourceUrl(create.getSourceUrl())
        .withDashboardType(create.getDashboardType())
        .withProject(create.getProject())
        .withSourceHash(create.getSourceHash());
  }
}
