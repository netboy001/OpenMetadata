package org.umetadata.service.resources.domains;

import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import java.util.List;
import org.umetadata.schema.api.domains.CreateDataProduct;
import org.umetadata.schema.entity.domains.DataProduct;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.EntityUtil;

public class DataProductMapper implements EntityMapper<DataProduct, CreateDataProduct> {
  @Override
  public DataProduct createToEntity(CreateDataProduct create, String user) {
    List<String> experts = create.getExperts();
    return copy(new DataProduct(), create, user)
        .withFullyQualifiedName(create.getName())
        .withStyle(create.getStyle())
        .withExperts(
            EntityUtil.validateAndPopulateEntityReferences(
                getEntityReferences(Entity.USER, experts)));
  }
}
