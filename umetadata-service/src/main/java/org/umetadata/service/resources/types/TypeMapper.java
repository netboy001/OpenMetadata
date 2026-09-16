package org.umetadata.service.resources.types;

import org.umetadata.schema.api.CreateType;
import org.umetadata.schema.entity.Type;
import org.umetadata.service.mapper.EntityMapper;

public class TypeMapper implements EntityMapper<Type, CreateType> {
  @Override
  public Type createToEntity(CreateType create, String user) {
    return copy(new Type(), create, user)
        .withFullyQualifiedName(create.getName())
        .withCategory(create.getCategory())
        .withSchema(create.getSchema());
  }
}
