package org.umetadata.service.resources.teams;

import static org.umetadata.schema.type.Include.NON_DELETED;
import static org.umetadata.service.exception.CatalogExceptionMessage.CREATE_GROUP;
import static org.umetadata.service.exception.CatalogExceptionMessage.CREATE_ORGANIZATION;

import org.umetadata.schema.api.teams.CreateTeam;
import org.umetadata.schema.entity.teams.Team;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.EntityUtil;

public class TeamMapper implements EntityMapper<Team, CreateTeam> {
  @Override
  public Team createToEntity(CreateTeam create, String user) {
    if (create.getTeamType().equals(CreateTeam.TeamType.ORGANIZATION)) {
      throw new IllegalArgumentException(CREATE_ORGANIZATION);
    }
    if (create.getTeamType().equals(CreateTeam.TeamType.GROUP) && create.getChildren() != null) {
      throw new IllegalArgumentException(CREATE_GROUP);
    }
    return copy(new Team(), create, user)
        .withProfile(create.getProfile())
        .withIsJoinable(create.getIsJoinable())
        .withUsers(EntityUtil.validateToEntityReferences(create.getUsers(), Entity.USER))
        .withDefaultRoles(
            EntityUtil.validateToEntityReferences(create.getDefaultRoles(), Entity.ROLE))
        .withTeamType(create.getTeamType())
        .withParents(EntityUtil.validateToEntityReferences(create.getParents(), Entity.TEAM))
        .withChildren(EntityUtil.validateToEntityReferences(create.getChildren(), Entity.TEAM))
        .withPolicies(EntityUtil.validateToEntityReferences(create.getPolicies(), Entity.POLICY))
        .withDefaultPersona(
            create.getDefaultPersona() != null
                ? Entity.getEntityReferenceById(
                    Entity.PERSONA, create.getDefaultPersona(), NON_DELETED)
                : null)
        .withEmail(create.getEmail())
        .withDomains(EntityUtil.getEntityReferences(Entity.DOMAIN, create.getDomains()));
  }
}
