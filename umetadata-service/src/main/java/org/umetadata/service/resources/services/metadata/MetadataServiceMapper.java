package org.umetadata.service.resources.services.metadata;

import org.umetadata.schema.api.services.CreateMetadataService;
import org.umetadata.schema.entity.services.MetadataService;
import org.umetadata.service.mapper.EntityMapper;

public class MetadataServiceMapper implements EntityMapper<MetadataService, CreateMetadataService> {
  @Override
  public MetadataService createToEntity(CreateMetadataService create, String user) {
    return copy(new MetadataService(), create, user)
        .withServiceType(create.getServiceType())
        .withConnection(create.getConnection())
        .withIngestionRunner(create.getIngestionRunner());
  }
}
