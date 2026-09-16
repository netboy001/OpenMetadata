package org.umetadata.sdk.services.glossary;

import org.umetadata.schema.api.data.CreateGlossaryTerm;
import org.umetadata.schema.entity.data.GlossaryTerm;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class GlossaryTermService extends EntityServiceBase<GlossaryTerm> {
  public GlossaryTermService(HttpClient httpClient) {
    super(httpClient, "/v1/glossaryTerms");
  }

  @Override
  protected Class<GlossaryTerm> getEntityClass() {
    return GlossaryTerm.class;
  }

  // Create glossaryterm using CreateGlossaryTerm request
  public GlossaryTerm create(CreateGlossaryTerm request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, GlossaryTerm.class);
  }
}
