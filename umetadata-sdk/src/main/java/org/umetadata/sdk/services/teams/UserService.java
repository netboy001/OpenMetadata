package org.umetadata.sdk.services.teams;

import org.umetadata.schema.api.teams.CreateUser;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class UserService extends EntityServiceBase<User> {
  public UserService(HttpClient httpClient) {
    super(httpClient, "/v1/users");
  }

  @Override
  protected Class<User> getEntityClass() {
    return User.class;
  }

  // Create user using CreateUser request
  public User create(CreateUser request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, User.class);
  }
}
