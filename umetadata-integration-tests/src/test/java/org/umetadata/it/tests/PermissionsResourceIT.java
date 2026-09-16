package org.umetadata.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.umetadata.it.factories.DatabaseSchemaTestFactory;
import org.umetadata.it.factories.DatabaseServiceTestFactory;
import org.umetadata.it.factories.PolicyTestFactory;
import org.umetadata.it.factories.UserTestFactory;
import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.schema.api.data.CreateTable;
import org.umetadata.schema.api.teams.CreateTeam;
import org.umetadata.schema.entity.data.DatabaseSchema;
import org.umetadata.schema.entity.data.Table;
import org.umetadata.schema.entity.policies.Policy;
import org.umetadata.schema.entity.services.DatabaseService;
import org.umetadata.schema.entity.teams.Team;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.MetadataOperation;
import org.umetadata.schema.type.Permission;
import org.umetadata.schema.type.ResourcePermission;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.fluent.builders.ColumnBuilder;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.service.security.policyevaluator.PermissionDebugInfo;

/**
 * Integration tests for Permissions resource operations.
 *
 * <p>Tests permission operations including: - Getting permissions for logged-in user - Getting
 * permissions for specific resource types - Getting permissions for specific entities - Getting
 * permissions by entity name - Getting permissions for policies - Permission access control (admin
 * vs non-admin)
 *
 * <p>Test isolation: Uses TestNamespace for unique entity names Parallelization: Safe for
 * concurrent execution via @Execution(ExecutionMode.CONCURRENT)
 *
 * <p>Migrated from: org.umetadata.service.resources.permissions.PermissionsResourceTest
 */
@Execution(ExecutionMode.CONCURRENT)
public class PermissionsResourceIT {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  void testGetPermissionsForLoggedInUser() throws Exception {
    UMetadataClient client = SdkClients.adminClient();

    List<ResourcePermission> permissions = getPermissions(client);

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());

    permissions.forEach(
        rp -> {
          assertNotNull(rp.getResource());
          assertNotNull(rp.getPermissions());
          assertFalse(rp.getPermissions().isEmpty());
        });
  }

  @Test
  void testGetPermissionsForResourceType() throws Exception {
    UMetadataClient client = SdkClients.adminClient();

    ResourcePermission permission = getPermissionForResource(client, "table");

    assertNotNull(permission);
    assertEquals("table", permission.getResource());
    assertNotNull(permission.getPermissions());
    assertFalse(permission.getPermissions().isEmpty());

    boolean hasViewOperation =
        permission.getPermissions().stream()
            .anyMatch(p -> p.getOperation() == MetadataOperation.VIEW_ALL);
    assertTrue(hasViewOperation, "Admin should have VIEW_ALL permission on tables");
  }

  @Test
  void testGetPermissionsForSpecificEntity() throws Exception {
    UMetadataClient client = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    Table table = createTestTable(client, ns, "test_permissions_table");

    ResourcePermission permission = getPermissionForEntity(client, "table", table.getId());

    assertNotNull(permission);
    assertEquals("table", permission.getResource());
    assertNotNull(permission.getPermissions());
    assertFalse(permission.getPermissions().isEmpty());

    boolean hasEditOperation =
        permission.getPermissions().stream()
            .anyMatch(
                p ->
                    p.getOperation() == MetadataOperation.EDIT_ALL
                        && p.getAccess() == Permission.Access.ALLOW);
    assertTrue(hasEditOperation, "Admin should have EDIT_ALL permission on specific table");

    cleanupTable(client, table);
  }

  @Test
  void testGetPermissionsForEntityByName() throws Exception {
    UMetadataClient client = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    Table table = createTestTable(client, ns, "test_permissions_by_name");

    ResourcePermission permission =
        getPermissionForEntityByName(client, "table", table.getFullyQualifiedName());

    assertNotNull(permission);
    assertEquals("table", permission.getResource());
    assertNotNull(permission.getPermissions());
    assertFalse(permission.getPermissions().isEmpty());

    boolean hasDeleteOperation =
        permission.getPermissions().stream()
            .anyMatch(
                p ->
                    p.getOperation() == MetadataOperation.DELETE
                        && p.getAccess() == Permission.Access.ALLOW);
    assertTrue(hasDeleteOperation, "Admin should have DELETE permission on specific table");

    cleanupTable(client, table);
  }

  @Test
  void testGetPermissionsForPolicies() throws Exception {
    UMetadataClient client = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    Policy dataConsumerPolicy = PolicyTestFactory.getDataConsumerPolicy(ns);
    assertNotNull(dataConsumerPolicy, "DataConsumer policy should exist");

    List<ResourcePermission> permissions =
        getPermissionsForPolicies(client, List.of(dataConsumerPolicy.getId()));

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());

    permissions.forEach(
        rp -> {
          assertNotNull(rp.getResource());
          assertNotNull(rp.getPermissions());
        });
  }

  @Test
  @org.junit.jupiter.api.Disabled("Requires DataConsumer/DataSteward users that may not exist")
  void testDataConsumerPermissionsOnNonOwnedTable() throws Exception {
    UMetadataClient dataConsumerClient = SdkClients.dataConsumerClient();
    UMetadataClient adminClient = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    User dataSteward = UserTestFactory.getDataSteward(ns);
    Table table = createTestTableWithOwner(adminClient, ns, "non_owned_table", dataSteward);

    ResourcePermission permission =
        getPermissionForEntity(dataConsumerClient, "table", table.getId());

    assertNotNull(permission);
    assertEquals("table", permission.getResource());

    boolean hasViewPermission =
        permission.getPermissions().stream()
            .anyMatch(
                p ->
                    p.getOperation() == MetadataOperation.VIEW_ALL
                        && p.getAccess() == Permission.Access.ALLOW);
    assertTrue(hasViewPermission, "DataConsumer should have VIEW permission on non-owned table");

    boolean hasEditAllPermission =
        permission.getPermissions().stream()
            .anyMatch(
                p ->
                    p.getOperation() == MetadataOperation.EDIT_ALL
                        && p.getAccess() == Permission.Access.ALLOW);
    assertFalse(
        hasEditAllPermission,
        "DataConsumer should NOT have EDIT_ALL permission on non-owned table");

    cleanupTable(adminClient, table);
  }

  @Test
  @org.junit.jupiter.api.Disabled("Requires DataConsumer/DataSteward users that may not exist")
  void testDataConsumerPermissionsOnOwnedTable() throws Exception {
    UMetadataClient dataConsumerClient = SdkClients.dataConsumerClient();
    UMetadataClient adminClient = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    User dataConsumer = UserTestFactory.getDataConsumer(ns);
    Table table = createTestTableWithOwner(adminClient, ns, "owned_table", dataConsumer);

    ResourcePermission permission =
        getPermissionForEntity(dataConsumerClient, "table", table.getId());

    assertNotNull(permission);
    assertEquals("table", permission.getResource());

    boolean hasViewPermission =
        permission.getPermissions().stream()
            .anyMatch(
                p ->
                    p.getOperation() == MetadataOperation.VIEW_ALL
                        && p.getAccess() == Permission.Access.ALLOW);
    assertTrue(hasViewPermission, "DataConsumer should have VIEW permission on owned table");

    boolean hasEditDescriptionPermission =
        permission.getPermissions().stream()
            .anyMatch(
                p ->
                    p.getOperation() == MetadataOperation.EDIT_DESCRIPTION
                        && p.getAccess() == Permission.Access.ALLOW);
    assertTrue(
        hasEditDescriptionPermission,
        "DataConsumer should have EDIT_DESCRIPTION permission on owned table");

    cleanupTable(adminClient, table);
  }

  @Test
  @org.junit.jupiter.api.Disabled("Requires DataConsumer/DataSteward users that may not exist")
  void testNonAdminCannotGetAnotherUserPermissions() {
    UMetadataClient dataConsumerClient = SdkClients.dataConsumerClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    User dataSteward = UserTestFactory.getDataSteward(ns);

    assertThrows(
        Exception.class,
        () -> getPermissionsForUser(dataConsumerClient, dataSteward.getName()),
        "Non-admin user should not be able to get another user's permissions");
  }

  @Test
  @org.junit.jupiter.api.Disabled("User creation failure - needs UserTestFactory investigation")
  void testAdminCanGetAnotherUserPermissions() throws Exception {
    UMetadataClient adminClient = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    User dataConsumer = UserTestFactory.getDataConsumer(ns);

    List<ResourcePermission> permissions =
        getPermissionsForUser(adminClient, dataConsumer.getName());

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());
  }

  @Test
  void testGetPermissionsForMultiplePolicies() throws Exception {
    UMetadataClient adminClient = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    Policy dataConsumerPolicy = PolicyTestFactory.getDataConsumerPolicy(ns);
    Policy dataStewardPolicy = PolicyTestFactory.getDataStewardPolicy(ns);

    List<ResourcePermission> permissions =
        getPermissionsForPolicies(
            adminClient, List.of(dataConsumerPolicy.getId(), dataStewardPolicy.getId()));

    assertNotNull(permissions);
    assertFalse(permissions.isEmpty());

    permissions.forEach(
        rp -> {
          assertNotNull(rp.getResource());
          assertNotNull(rp.getPermissions());

          List<Permission> allowedPerms =
              rp.getPermissions().stream()
                  .filter(p -> p.getAccess() == Permission.Access.ALLOW)
                  .toList();
          assertFalse(allowedPerms.isEmpty(), "Should have some ALLOW permissions from policies");
        });
  }

  @Test
  void test_debugPermissions_adminCanDebugAnyUser() throws Exception {
    UMetadataClient adminClient = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    User testUser = UserTestFactory.createUser(ns, "test-debug-user");
    Team testTeam = createTestTeam(adminClient, ns, "test-debug-team");

    addUserToTeam(adminClient, testTeam, testUser);

    PermissionDebugInfo debugInfo = getDebugPermissionsForUser(adminClient, testUser.getName());

    assertEquals(testUser.getName(), debugInfo.getUser().getName());
    assertNotNull(debugInfo.getDirectRoles());
    assertNotNull(debugInfo.getTeamPermissions());
    assertNotNull(debugInfo.getSummary());

    cleanupUser(adminClient, testUser);
    cleanupTeam(adminClient, testTeam);
  }

  @Test
  void test_debugPermissions_userCanDebugOwnPermissions() throws Exception {
    UMetadataClient adminClient = SdkClients.adminClient();
    TestNamespace ns = new TestNamespace("PermissionsResourceIT");

    User testUser = UserTestFactory.createUser(ns, "test-debug-own-user");

    PermissionDebugInfo debugInfo = getDebugPermissionsForUser(adminClient, testUser.getName());

    assertEquals(testUser.getName(), debugInfo.getUser().getName());
    assertNotNull(debugInfo.getDirectRoles());
    assertNotNull(debugInfo.getTeamPermissions());
    assertNotNull(debugInfo.getSummary());

    cleanupUser(adminClient, testUser);
  }

  @Test
  void test_debugPermissions_userCannotDebugOtherUserPermissions() throws Exception {
    UMetadataClient user1Client = SdkClients.user1Client();
    org.umetadata.it.bootstrap.SharedEntities shared =
        org.umetadata.it.bootstrap.SharedEntities.get();

    // user1 tries to debug user2's permissions - should fail
    assertThrows(
        Exception.class,
        () -> getDebugPermissionsForUser(user1Client, shared.USER2.getName()),
        "User should not be able to debug another user's permissions");
  }

  @Test
  void test_debugMyPermissions() throws Exception {
    UMetadataClient adminClient = SdkClients.adminClient();

    PermissionDebugInfo debugInfo = getDebugMyPermissions(adminClient);

    assertNotNull(debugInfo.getUser());
    assertNotNull(debugInfo.getUser().getName());
    assertNotNull(debugInfo.getDirectRoles());
    assertNotNull(debugInfo.getTeamPermissions());
    assertNotNull(debugInfo.getSummary());
  }

  private List<ResourcePermission> getPermissions(UMetadataClient client) throws Exception {
    String response =
        client.getHttpClient().executeForString(HttpMethod.GET, "/v1/permissions", null);
    ResourcePermissionListWrapper wrapper =
        OBJECT_MAPPER.readValue(response, ResourcePermissionListWrapper.class);
    return wrapper.getData();
  }

  private List<ResourcePermission> getPermissionsForUser(UMetadataClient client, String username)
      throws Exception {
    String response =
        client
            .getHttpClient()
            .executeForString(HttpMethod.GET, "/v1/permissions?user=" + username, null);
    ResourcePermissionListWrapper wrapper =
        OBJECT_MAPPER.readValue(response, ResourcePermissionListWrapper.class);
    return wrapper.getData();
  }

  private ResourcePermission getPermissionForResource(UMetadataClient client, String resource)
      throws Exception {
    String response =
        client
            .getHttpClient()
            .executeForString(HttpMethod.GET, "/v1/permissions/" + resource, null);
    return OBJECT_MAPPER.readValue(response, ResourcePermission.class);
  }

  private ResourcePermission getPermissionForEntity(
      UMetadataClient client, String resource, UUID entityId) throws Exception {
    String response =
        client
            .getHttpClient()
            .executeForString(
                HttpMethod.GET, "/v1/permissions/" + resource + "/" + entityId.toString(), null);
    return OBJECT_MAPPER.readValue(response, ResourcePermission.class);
  }

  private ResourcePermission getPermissionForEntityByName(
      UMetadataClient client, String resource, String entityName) throws Exception {
    String response =
        client
            .getHttpClient()
            .executeForString(
                HttpMethod.GET, "/v1/permissions/" + resource + "/name/" + entityName, null);
    return OBJECT_MAPPER.readValue(response, ResourcePermission.class);
  }

  private List<ResourcePermission> getPermissionsForPolicies(
      UMetadataClient client, List<UUID> policyIds) throws Exception {
    StringBuilder queryParams = new StringBuilder();
    for (int i = 0; i < policyIds.size(); i++) {
      if (i > 0) {
        queryParams.append("&");
      }
      queryParams.append("ids=").append(policyIds.get(i).toString());
    }

    String response =
        client
            .getHttpClient()
            .executeForString(HttpMethod.GET, "/v1/permissions/policies?" + queryParams, null);
    ResourcePermissionListWrapper wrapper =
        OBJECT_MAPPER.readValue(response, ResourcePermissionListWrapper.class);
    return wrapper.getData();
  }

  private Table createTestTable(UMetadataClient client, TestNamespace ns, String tableName)
      throws Exception {
    DatabaseService service = DatabaseServiceTestFactory.createPostgres(ns);
    DatabaseSchema schema = DatabaseSchemaTestFactory.createSimple(ns, service);

    CreateTable createTable = new CreateTable();
    createTable.setName(ns.prefix(tableName));
    createTable.setDatabaseSchema(schema.getFullyQualifiedName());
    createTable.setDescription("Test table for permissions testing");
    createTable.setColumns(
        List.of(
            ColumnBuilder.of("id", "BIGINT").primaryKey().notNull().build(),
            ColumnBuilder.of("name", "VARCHAR").dataLength(255).build()));

    return client.tables().create(createTable);
  }

  private Table createTestTableWithOwner(
      UMetadataClient client, TestNamespace ns, String tableName, User owner) throws Exception {
    DatabaseService service = DatabaseServiceTestFactory.createPostgres(ns);
    DatabaseSchema schema = DatabaseSchemaTestFactory.createSimple(ns, service);

    CreateTable createTable = new CreateTable();
    createTable.setName(ns.prefix(tableName));
    createTable.setDatabaseSchema(schema.getFullyQualifiedName());
    createTable.setDescription("Test table with owner for permissions testing");
    createTable.setColumns(
        List.of(
            ColumnBuilder.of("id", "BIGINT").primaryKey().notNull().build(),
            ColumnBuilder.of("name", "VARCHAR").dataLength(255).build()));
    createTable.setOwners(List.of(owner.getEntityReference()));

    return client.tables().create(createTable);
  }

  private void cleanupTable(UMetadataClient client, Table table) throws Exception {
    try {
      client.tables().delete(table.getId().toString());
    } catch (UMetadataException e) {
      // Ignore cleanup errors
    }
  }

  private Team createTestTeam(UMetadataClient client, TestNamespace ns, String teamName)
      throws Exception {
    CreateTeam createTeam = new CreateTeam();
    createTeam.setName(ns.prefix(teamName));
    createTeam.setDisplayName(ns.prefix(teamName));
    createTeam.setDescription("Test team for permissions testing");
    createTeam.setTeamType(CreateTeam.TeamType.GROUP);
    return client.teams().create(createTeam);
  }

  private void addUserToTeam(UMetadataClient client, Team team, User user) throws Exception {
    User fetchedUser = client.users().get(user.getId().toString(), "teams");
    List<EntityReference> teams = new ArrayList<>();
    if (fetchedUser.getTeams() != null) {
      teams.addAll(fetchedUser.getTeams());
    }
    teams.add(team.getEntityReference());
    fetchedUser.setTeams(teams);
    client.users().update(user.getId(), fetchedUser);
  }

  private void cleanupUser(UMetadataClient client, User user) throws Exception {
    try {
      client.users().delete(user.getId().toString());
    } catch (UMetadataException e) {
      // Ignore cleanup errors
    }
  }

  private void cleanupTeam(UMetadataClient client, Team team) throws Exception {
    try {
      client.teams().delete(team.getId().toString());
    } catch (UMetadataException e) {
      // Ignore cleanup errors
    }
  }

  private PermissionDebugInfo getDebugPermissionsForUser(UMetadataClient client, String username)
      throws Exception {
    String response =
        client
            .getHttpClient()
            .executeForString(HttpMethod.GET, "/v1/permissions/debug/user/" + username, null);
    return OBJECT_MAPPER.readValue(response, PermissionDebugInfo.class);
  }

  private PermissionDebugInfo getDebugMyPermissions(UMetadataClient client) throws Exception {
    String response =
        client.getHttpClient().executeForString(HttpMethod.GET, "/v1/permissions/debug/me", null);
    return OBJECT_MAPPER.readValue(response, PermissionDebugInfo.class);
  }

  private static class ResourcePermissionListWrapper {
    private List<ResourcePermission> data;

    public List<ResourcePermission> getData() {
      return data;
    }

    public void setData(List<ResourcePermission> data) {
      this.data = data;
    }
  }
}
