package org.umetadata.service.resources.services.mlmodel;

import org.umetadata.schema.api.services.CreateMlModelService;
import org.umetadata.schema.entity.services.MlModelService;
import org.umetadata.service.mapper.EntityMapper;

public class MlModelServiceMapper implements EntityMapper<MlModelService, CreateMlModelService> {
  @Override
  public MlModelService createToEntity(CreateMlModelService create, String user) {
    return copy(new MlModelService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
