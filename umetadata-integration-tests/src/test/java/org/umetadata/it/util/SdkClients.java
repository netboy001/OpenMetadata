package org.umetadata.it.util;

import org.umetadata.it.auth.JwtAuthProvider;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.config.UMetadataConfig;
import org.umetadata.sdk.fluent.AIApplications;
import org.umetadata.sdk.fluent.Apps;
import org.umetadata.sdk.fluent.Charts;
import org.umetadata.sdk.fluent.Classifications;
import org.umetadata.sdk.fluent.Containers;
import org.umetadata.sdk.fluent.DashboardDataModels;
import org.umetadata.sdk.fluent.DashboardServices;
import org.umetadata.sdk.fluent.Dashboards;
import org.umetadata.sdk.fluent.DataProducts;
import org.umetadata.sdk.fluent.DatabaseSchemas;
import org.umetadata.sdk.fluent.DatabaseServices;
import org.umetadata.sdk.fluent.Databases;
import org.umetadata.sdk.fluent.Directories;
import org.umetadata.sdk.fluent.Domains;
import org.umetadata.sdk.fluent.Files;
import org.umetadata.sdk.fluent.Glossaries;
import org.umetadata.sdk.fluent.GlossaryTerms;
import org.umetadata.sdk.fluent.MessagingServices;
import org.umetadata.sdk.fluent.Metrics;
import org.umetadata.sdk.fluent.MlModelServices;
import org.umetadata.sdk.fluent.MlModels;
import org.umetadata.sdk.fluent.Personas;
import org.umetadata.sdk.fluent.PipelineServices;
import org.umetadata.sdk.fluent.Pipelines;
import org.umetadata.sdk.fluent.Queries;
import org.umetadata.sdk.fluent.Roles;
import org.umetadata.sdk.fluent.SearchIndexes;
import org.umetadata.sdk.fluent.Spreadsheets;
import org.umetadata.sdk.fluent.StorageServices;
import org.umetadata.sdk.fluent.StoredProcedures;
import org.umetadata.sdk.fluent.Tables;
import org.umetadata.sdk.fluent.Tags;
import org.umetadata.sdk.fluent.Teams;
import org.umetadata.sdk.fluent.TestCases;
import org.umetadata.sdk.fluent.Topics;
import org.umetadata.sdk.fluent.Usage;
import org.umetadata.sdk.fluent.Users;
import org.umetadata.sdk.fluent.Worksheets;

public class SdkClients {

  private static final String BASE_URL =
      System.getProperty(
          "IT_BASE_URL", System.getenv().getOrDefault("IT_BASE_URL", "http://localhost:8585"));

  // Cached clients to avoid creating new HTTP connections for each test
  private static volatile UMetadataClient ADMIN_CLIENT;
  private static volatile UMetadataClient TEST_USER_CLIENT;
  private static volatile UMetadataClient BOT_CLIENT;
  private static volatile UMetadataClient DATA_STEWARD_CLIENT;
  private static volatile UMetadataClient DATA_CONSUMER_CLIENT;
  private static volatile UMetadataClient USER1_CLIENT;
  private static volatile UMetadataClient USER2_CLIENT;
  private static volatile UMetadataClient USER3_CLIENT;

  public static UMetadataClient adminClient() {
    if (ADMIN_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (ADMIN_CLIENT == null) {
          ADMIN_CLIENT =
              createClient(
                  "admin@u-metadata.org", "admin@u-metadata.org", new String[] {"admin"});
        }
      }
    }
    return ADMIN_CLIENT;
  }

  public static UMetadataClient testUserClient() {
    if (TEST_USER_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (TEST_USER_CLIENT == null) {
          TEST_USER_CLIENT =
              createClient("test@u-metadata.org", "test@u-metadata.org", new String[] {});
        }
      }
    }
    return TEST_USER_CLIENT;
  }

  public static UMetadataClient botClient() {
    if (BOT_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (BOT_CLIENT == null) {
          BOT_CLIENT =
              createClient(
                  "ingestion-bot@u-metadata.org",
                  "ingestion-bot@u-metadata.org",
                  new String[] {"bot"});
        }
      }
    }
    return BOT_CLIENT;
  }

  public static UMetadataClient ingestionBotClient() {
    return botClient();
  }

  public static UMetadataClient dataStewardClient() {
    if (DATA_STEWARD_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (DATA_STEWARD_CLIENT == null) {
          DATA_STEWARD_CLIENT =
              createClient(
                  "data-steward@u-metadata.org",
                  "data-steward@u-metadata.org",
                  new String[] {"DataSteward"});
        }
      }
    }
    return DATA_STEWARD_CLIENT;
  }

  public static UMetadataClient dataConsumerClient() {
    if (DATA_CONSUMER_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (DATA_CONSUMER_CLIENT == null) {
          DATA_CONSUMER_CLIENT =
              createClient(
                  "data-consumer@u-metadata.org",
                  "data-consumer@u-metadata.org",
                  new String[] {"DataConsumer"});
        }
      }
    }
    return DATA_CONSUMER_CLIENT;
  }

  public static UMetadataClient user1Client() {
    if (USER1_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (USER1_CLIENT == null) {
          // USER1 has AllowAll role assigned in SharedEntities for permission tests
          USER1_CLIENT =
              createClient(
                  "shared_user1@test.umetadata.org",
                  "shared_user1@test.umetadata.org",
                  new String[] {});
        }
      }
    }
    return USER1_CLIENT;
  }

  public static UMetadataClient user2Client() {
    if (USER2_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (USER2_CLIENT == null) {
          USER2_CLIENT =
              createClient(
                  "shared_user2@test.umetadata.org",
                  "shared_user2@test.umetadata.org",
                  new String[] {});
        }
      }
    }
    return USER2_CLIENT;
  }

  public static UMetadataClient user3Client() {
    if (USER3_CLIENT == null) {
      synchronized (SdkClients.class) {
        if (USER3_CLIENT == null) {
          USER3_CLIENT =
              createClient(
                  "shared_user3@test.umetadata.org",
                  "shared_user3@test.umetadata.org",
                  new String[] {});
        }
      }
    }
    return USER3_CLIENT;
  }

  /**
   * Create a new client for a specific user. Note: For standard users (admin, test, bot, etc.),
   * prefer using the cached methods like adminClient(), testUserClient(), etc. to avoid
   * creating too many HTTP connections during parallel test execution.
   */
  public static UMetadataClient createClient(String subject, String email, String[] roles) {
    String token = JwtAuthProvider.tokenFor(subject, email, roles, 3600);
    UMetadataConfig cfg =
        UMetadataConfig.builder()
            .serverUrl(BASE_URL)
            .accessToken(token)
            .header("X-Auth-Params-Email", email)
            .readTimeout(300000)
            .writeTimeout(300000)
            .build();
    UMetadataClient client = new UMetadataClient(cfg);
    // Set default client for fluent APIs used in factories (only for admin)
    if (email.equals("admin@u-metadata.org")) {
      initializeFluentAPIs(client);
    }
    return client;
  }

  /**
   * Initialize all fluent API classes with the default client.
   * This allows using static methods like Tables.find(id).fetch()
   */
  private static void initializeFluentAPIs(UMetadataClient client) {
    // AI
    AIApplications.setDefaultClient(client);

    // Apps
    Apps.setDefaultClient(client);

    // Data Assets
    Charts.setDefaultClient(client);
    Containers.setDefaultClient(client);
    DashboardDataModels.setDefaultClient(client);
    Dashboards.setDefaultClient(client);
    Databases.setDefaultClient(client);
    DatabaseSchemas.setDefaultClient(client);
    Directories.setDefaultClient(client);
    Files.setDefaultClient(client);
    MlModels.setDefaultClient(client);
    Pipelines.setDefaultClient(client);
    Queries.setDefaultClient(client);
    SearchIndexes.setDefaultClient(client);
    Spreadsheets.setDefaultClient(client);
    StoredProcedures.setDefaultClient(client);
    Tables.setDefaultClient(client);
    Topics.setDefaultClient(client);
    Usage.setDefaultClient(client);
    Worksheets.setDefaultClient(client);

    // Services
    DashboardServices.setDefaultClient(client);
    DatabaseServices.setDefaultClient(client);
    MessagingServices.setDefaultClient(client);
    MlModelServices.setDefaultClient(client);
    PipelineServices.setDefaultClient(client);
    StorageServices.setDefaultClient(client);

    // Teams & Users
    Personas.setDefaultClient(client);
    Roles.setDefaultClient(client);
    Teams.setDefaultClient(client);
    Users.setDefaultClient(client);

    // Governance
    Classifications.setDefaultClient(client);
    DataProducts.setDefaultClient(client);
    Domains.setDefaultClient(client);
    Glossaries.setDefaultClient(client);
    GlossaryTerms.setDefaultClient(client);
    Metrics.setDefaultClient(client);
    Tags.setDefaultClient(client);
    TestCases.setDefaultClient(client);
  }

  /** Get the base server URL for direct HTTP calls */
  public static String getServerUrl() {
    return BASE_URL;
  }

  /** Get an admin JWT token for direct HTTP calls */
  public static String getAdminToken() {
    return JwtAuthProvider.tokenFor(
        "admin@u-metadata.org", "admin@u-metadata.org", new String[] {"admin"}, 3600);
  }
}
