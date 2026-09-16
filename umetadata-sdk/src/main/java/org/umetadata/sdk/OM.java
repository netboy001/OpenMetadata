package org.umetadata.sdk;

import org.umetadata.sdk.client.UMetadataClient;

/**
 * OM (UMetadata) - Main entry point for the SDK with pure fluent API.
 *
 * This class provides a convenient wrapper around the pure fluent API classes.
 *
 * Usage:
 * <pre>
 * // Initialize
 * UMetadataClient client = new UMetadataClient(config);
 * OM.init(client);
 *
 * // Use fluent API through OM
 * Table table = OM.Table.find(id).fetch();
 * Database db = OM.Database.find(id).includeOwners().fetch();
 * </pre>
 */
public class OM {
  private static UMetadataClient client;

  public static void init(UMetadataClient client) {
    OM.client = client;
    // Initialize all pure fluent API classes
    org.umetadata.sdk.fluent.Tables.setDefaultClient(client);
    org.umetadata.sdk.fluent.Databases.setDefaultClient(client);
    org.umetadata.sdk.fluent.DatabaseSchemas.setDefaultClient(client);
    org.umetadata.sdk.fluent.Users.setDefaultClient(client);
    org.umetadata.sdk.fluent.Teams.setDefaultClient(client);
    org.umetadata.sdk.fluent.Dashboards.setDefaultClient(client);
    org.umetadata.sdk.fluent.Charts.setDefaultClient(client);
    org.umetadata.sdk.fluent.DashboardDataModels.setDefaultClient(client);
    org.umetadata.sdk.fluent.Pipelines.setDefaultClient(client);
    org.umetadata.sdk.fluent.Topics.setDefaultClient(client);
    org.umetadata.sdk.fluent.Containers.setDefaultClient(client);
    org.umetadata.sdk.fluent.StorageServices.setDefaultClient(client);
    org.umetadata.sdk.fluent.MlModels.setDefaultClient(client);
    org.umetadata.sdk.fluent.Queries.setDefaultClient(client);
    org.umetadata.sdk.fluent.SearchIndexes.setDefaultClient(client);
    org.umetadata.sdk.fluent.StoredProcedures.setDefaultClient(client);
    org.umetadata.sdk.fluent.Glossaries.setDefaultClient(client);
    org.umetadata.sdk.fluent.GlossaryTerms.setDefaultClient(client);
    org.umetadata.sdk.fluent.Classifications.setDefaultClient(client);
    org.umetadata.sdk.fluent.Tags.setDefaultClient(client);
    org.umetadata.sdk.fluent.DataProducts.setDefaultClient(client);
    org.umetadata.sdk.fluent.Domains.setDefaultClient(client);
    org.umetadata.sdk.fluent.Metrics.setDefaultClient(client);
    org.umetadata.sdk.fluent.TestCases.setDefaultClient(client);

    // Initialize new fluent API classes
    org.umetadata.sdk.api.Search.setDefaultClient(client);
    org.umetadata.sdk.api.Lineage.setDefaultClient(client);
    org.umetadata.sdk.api.Bulk.setDefaultClient(client);

    // Initialize old entity classes that still exist
    org.umetadata.sdk.entities.DatabaseService.setDefaultClient(client);
    org.umetadata.sdk.entities.TestCase.setDefaultClient(client);
    org.umetadata.sdk.entities.SearchIndex.setDefaultClient(client);
  }

  // Wrapper classes that delegate to pure fluent API

  public static class Table {
    public static org.umetadata.sdk.fluent.Tables.TableFinder find(String id) {
      return org.umetadata.sdk.fluent.Tables.find(id);
    }

    public static org.umetadata.sdk.fluent.Tables.TableFinder findByName(String fqn) {
      return org.umetadata.sdk.fluent.Tables.findByName(fqn);
    }

    public static org.umetadata.schema.entity.data.Table create(
        org.umetadata.schema.api.data.CreateTable request) {
      return org.umetadata.sdk.fluent.Tables.create(request);
    }
  }

  public static class Database {
    public static org.umetadata.sdk.fluent.Databases.DatabaseFinder find(String id) {
      return org.umetadata.sdk.fluent.Databases.find(id);
    }

    public static org.umetadata.sdk.fluent.Databases.DatabaseFinder findByName(String fqn) {
      return org.umetadata.sdk.fluent.Databases.findByName(fqn);
    }

    public static org.umetadata.schema.entity.data.Database create(
        org.umetadata.schema.api.data.CreateDatabase request) {
      return org.umetadata.sdk.fluent.Databases.create(request);
    }
  }

  public static class DatabaseService {
    // DatabaseService still uses old entity pattern
    public static org.umetadata.schema.entity.services.DatabaseService create(
        org.umetadata.schema.api.services.CreateDatabaseService request) {
      return org.umetadata.sdk.entities.DatabaseService.create(request);
    }

    public static org.umetadata.sdk.fluent.Databases.DatabaseFinder find(String id) {
      return org.umetadata.sdk.fluent.Databases.find(id);
    }
  }

  public static class User {
    public static org.umetadata.sdk.fluent.Users.UserFinder find(String id) {
      return org.umetadata.sdk.fluent.Users.find(id);
    }

    public static org.umetadata.sdk.fluent.Users.UserFinder findByName(String fqn) {
      return org.umetadata.sdk.fluent.Users.findByName(fqn);
    }

    public static org.umetadata.schema.entity.teams.User create(
        org.umetadata.schema.api.teams.CreateUser request) {
      return org.umetadata.sdk.fluent.Users.create(request);
    }
  }

  public static class Team {
    public static org.umetadata.sdk.fluent.Teams.TeamFinder find(String id) {
      return org.umetadata.sdk.fluent.Teams.find(id);
    }

    public static org.umetadata.sdk.fluent.Teams.TeamFinder findByName(String fqn) {
      return org.umetadata.sdk.fluent.Teams.findByName(fqn);
    }

    public static org.umetadata.schema.entity.teams.Team create(
        org.umetadata.schema.api.teams.CreateTeam request) {
      return org.umetadata.sdk.fluent.Teams.create(request);
    }
  }

  public static class Dashboard {
    public static org.umetadata.sdk.fluent.Dashboards.DashboardFinder find(String id) {
      return org.umetadata.sdk.fluent.Dashboards.find(id);
    }

    public static org.umetadata.sdk.fluent.Dashboards.DashboardFinder findByName(String fqn) {
      return org.umetadata.sdk.fluent.Dashboards.findByName(fqn);
    }

    public static org.umetadata.schema.entity.data.Dashboard create(
        org.umetadata.schema.api.data.CreateDashboard request) {
      return org.umetadata.sdk.fluent.Dashboards.create(request);
    }
  }

  // Add more wrapper classes as needed for other entities
}
