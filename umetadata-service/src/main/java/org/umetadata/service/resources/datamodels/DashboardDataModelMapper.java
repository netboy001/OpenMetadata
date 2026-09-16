package org.umetadata.service.resources.datamodels;

import org.umetadata.schema.api.data.CreateDashboardDataModel;
import org.umetadata.schema.entity.data.DashboardDataModel;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.resources.databases.DatabaseUtil;
import org.umetadata.service.util.EntityUtil;

public class DashboardDataModelMapper
    implements EntityMapper<DashboardDataModel, CreateDashboardDataModel> {
  @Override
  public DashboardDataModel createToEntity(CreateDashboardDataModel create, String user) {
    DatabaseUtil.validateColumns(create.getColumns());
    return copy(new DashboardDataModel(), create, user)
        .withService(EntityUtil.getEntityReference(Entity.DASHBOARD_SERVICE, create.getService()))
        .withDataModelType(create.getDataModelType())
        .withSql(create.getSql())
        .withDataModelType(create.getDataModelType())
        .withServiceType(create.getServiceType())
        .withColumns(create.getColumns())
        .withProject(create.getProject())
        .withSourceHash(create.getSourceHash())
        .withSourceUrl(create.getSourceUrl());
  }
}
