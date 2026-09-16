package org.umetadata.service.util;

import static org.umetadata.service.Entity.ADMIN_USER_NAME;
import static org.umetadata.service.Entity.APPLICATION;
import static org.umetadata.service.jdbi3.AppRepository.APP_BOT_ROLE;
import static org.umetadata.service.jdbi3.EntityRepository.getEntitiesFromSeedData;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.entity.app.AppMarketPlaceDefinition;
import org.umetadata.schema.entity.app.CreateAppMarketPlaceDefinitionReq;
import org.umetadata.schema.entity.teams.Role;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.exception.EntityNotFoundException;
import org.umetadata.service.jdbi3.AppMarketPlaceRepository;
import org.umetadata.service.jdbi3.PolicyRepository;
import org.umetadata.service.jdbi3.RoleRepository;
import org.umetadata.service.jdbi3.TeamRepository;
import org.umetadata.service.resources.apps.AppMarketPlaceMapper;

@Slf4j
public class AppMarketPlaceUtil {
  public static void createAppMarketPlaceDefinitions(
      AppMarketPlaceRepository appMarketRepository, AppMarketPlaceMapper mapper)
      throws IOException {
    PolicyRepository policyRepository = Entity.getPolicyRepository();
    RoleRepository roleRepository = Entity.getRoleRepository();

    try {
      roleRepository.findByName(APP_BOT_ROLE, Include.NON_DELETED);
    } catch (EntityNotFoundException e) {
      policyRepository.initSeedDataFromResources();
      List<Role> roles = roleRepository.getEntitiesFromSeedData();
      for (Role role : roles) {
        role.setFullyQualifiedName(role.getName());
        List<EntityReference> policies = role.getPolicies();
        for (EntityReference policy : policies) {
          EntityReference ref =
              Entity.getEntityReferenceByName(Entity.POLICY, policy.getName(), Include.NON_DELETED);
          policy.setId(ref.getId());
        }
        roleRepository.initializeEntity(role);
      }
      TeamRepository teamRepository = (TeamRepository) Entity.getEntityRepository(Entity.TEAM);
      teamRepository.initOrganization();
    }

    getEntitiesFromSeedData(
            APPLICATION,
            String.format(".*json/data/%s/.*\\.json$", Entity.APP_MARKET_PLACE_DEF),
            CreateAppMarketPlaceDefinitionReq.class)
        .stream()
        .filter(
            req ->
                Optional.ofNullable(System.getenv("ENABLE_APP_" + req.getName()))
                    .map(val -> Objects.equals(val, "true"))
                    .orElse(req.getEnabled()))
        .filter(
            req -> {
              try {
                JsonUtils.validateJsonSchema(req, CreateAppMarketPlaceDefinitionReq.class);
                return true;
              } catch (ConstraintViolationException e) {
                LOG.error(
                    "Error validating {}: {}",
                    CreateAppMarketPlaceDefinitionReq.class.getSimpleName(),
                    req.getName(),
                    e);
                return false;
              }
            })
        .forEach(
            req -> {
              AppMarketPlaceDefinition definition = mapper.createToEntity(req, ADMIN_USER_NAME);
              appMarketRepository.setFullyQualifiedName(definition);
              appMarketRepository.createOrUpdate(null, definition, ADMIN_USER_NAME);
            });
  }
}
