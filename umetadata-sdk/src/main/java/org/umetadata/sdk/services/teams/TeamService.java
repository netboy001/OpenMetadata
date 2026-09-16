package org.umetadata.sdk.services.teams;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import org.umetadata.schema.api.teams.CreateTeam;
import org.umetadata.schema.entity.teams.Team;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class TeamService extends EntityServiceBase<Team> {
  public TeamService(HttpClient httpClient) {
    super(httpClient, "/v1/teams");
  }

  @Override
  protected Class<Team> getEntityClass() {
    return Team.class;
  }

  // Create team using CreateTeam request
  public Team create(CreateTeam request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Team.class);
  }

  public Map<String, Integer> getAllTeamsWithAssetsCount() throws UMetadataException {
    String responseStr =
        httpClient.executeForString(HttpMethod.GET, basePath + "/assets/counts", null, null);
    try {
      return objectMapper.readValue(responseStr, new TypeReference<Map<String, Integer>>() {});
    } catch (Exception e) {
      throw new UMetadataException(
          "Failed to deserialize getAllTeamsWithAssetsCount response: " + e.getMessage(), e);
    }
  }
}
