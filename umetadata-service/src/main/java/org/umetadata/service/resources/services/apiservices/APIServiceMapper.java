package org.umetadata.service.resources.services.apiservices;

import org.umetadata.schema.api.services.CreateApiService;
import org.umetadata.schema.entity.services.ApiService;
import org.umetadata.service.mapper.EntityMapper;

public class APIServiceMapper implements EntityMapper<ApiService, CreateApiService> {
  @Override
  public ApiService createToEntity(CreateApiService create, String user) {
    return copy(new ApiService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
