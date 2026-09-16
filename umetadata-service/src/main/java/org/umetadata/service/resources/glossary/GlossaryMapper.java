package org.umetadata.service.resources.glossary;

import org.umetadata.schema.api.data.CreateGlossary;
import org.umetadata.schema.entity.data.Glossary;
import org.umetadata.service.mapper.EntityMapper;

public class GlossaryMapper implements EntityMapper<Glossary, CreateGlossary> {
  @Override
  public Glossary createToEntity(CreateGlossary create, String user) {
    return copy(new Glossary(), create, user)
        .withProvider(create.getProvider())
        .withMutuallyExclusive(create.getMutuallyExclusive());
  }
}
