package org.umetadata.sdk.services.glossary;

import org.umetadata.schema.api.data.CreateGlossary;
import org.umetadata.schema.entity.data.Glossary;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class GlossaryService extends EntityServiceBase<Glossary> {
  public GlossaryService(HttpClient httpClient) {
    super(httpClient, "/v1/glossaries");
  }

  @Override
  protected Class<Glossary> getEntityClass() {
    return Glossary.class;
  }

  // Create glossary using CreateGlossary request
  public Glossary create(CreateGlossary request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Glossary.class);
  }
}
