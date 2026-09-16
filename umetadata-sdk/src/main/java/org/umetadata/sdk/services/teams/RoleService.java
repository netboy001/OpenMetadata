package org.umetadata.sdk.services.teams;

import org.umetadata.schema.api.teams.CreateRole;
import org.umetadata.schema.entity.teams.Role;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class RoleService extends EntityServiceBase<Role> {
  public RoleService(HttpClient httpClient) {
    super(httpClient, "/v1/roles");
  }

  @Override
  protected Class<Role> getEntityClass() {
    return Role.class;
  }

  public Role create(CreateRole request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Role.class);
  }
}
