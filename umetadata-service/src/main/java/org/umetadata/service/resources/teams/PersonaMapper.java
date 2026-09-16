package org.umetadata.service.resources.teams;

import org.umetadata.schema.api.teams.CreatePersona;
import org.umetadata.schema.entity.teams.Persona;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.EntityUtil;

public class PersonaMapper implements EntityMapper<Persona, CreatePersona> {
  @Override
  public Persona createToEntity(CreatePersona create, String user) {
    return copy(new Persona(), create, user)
        .withUsers(EntityUtil.validateToEntityReferences(create.getUsers(), Entity.USER))
        .withDefault(create.getDefault());
  }
}
