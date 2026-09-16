package org.umetadata.sdk.services.domains;

import org.umetadata.schema.api.domains.CreateDomain;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DomainService
    extends EntityServiceBase<org.umetadata.schema.entity.domains.Domain> {

  public DomainService(HttpClient httpClient) {
    super(httpClient, "/v1/domains");
  }

  @Override
  protected Class<org.umetadata.schema.entity.domains.Domain> getEntityClass() {
    return org.umetadata.schema.entity.domains.Domain.class;
  }

  // Create using CreateDomain request
  public org.umetadata.schema.entity.domains.Domain create(CreateDomain request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST, basePath, request, org.umetadata.schema.entity.domains.Domain.class);
  }
}
