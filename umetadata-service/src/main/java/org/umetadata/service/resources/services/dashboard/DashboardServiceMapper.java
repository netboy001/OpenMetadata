package org.umetadata.service.resources.services.dashboard;

import org.umetadata.schema.api.services.CreateDashboardService;
import org.umetadata.schema.entity.services.DashboardService;
import org.umetadata.service.mapper.EntityMapper;

public class DashboardServiceMapper
    implements EntityMapper<DashboardService, CreateDashboardService> {
  @Override
  public DashboardService createToEntity(CreateDashboardService create, String user) {
    return copy(new DashboardService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
