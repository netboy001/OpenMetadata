package org.umetadata.sdk.services.teams;

import org.umetadata.schema.api.teams.CreatePersona;
import org.umetadata.schema.entity.teams.Persona;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class PersonaService extends EntityServiceBase<Persona> {
  public PersonaService(HttpClient httpClient) {
    super(httpClient, "/v1/personas");
  }

  @Override
  protected Class<Persona> getEntityClass() {
    return Persona.class;
  }

  public Persona create(CreatePersona request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Persona.class);
  }
}
