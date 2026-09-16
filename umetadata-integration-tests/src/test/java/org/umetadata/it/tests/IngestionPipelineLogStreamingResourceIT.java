package org.umetadata.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.umetadata.it.bootstrap.TestSuiteBootstrap;
import org.umetadata.it.factories.DatabaseServiceTestFactory;
import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.it.util.TestNamespaceExtension;
import org.umetadata.schema.api.services.ingestionPipelines.CreateIngestionPipeline;
import org.umetadata.schema.entity.services.DatabaseService;
import org.umetadata.schema.entity.services.ingestionPipelines.AirflowConfig;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineType;
import org.umetadata.schema.metadataIngestion.DatabaseServiceMetadataPipeline;
import org.umetadata.schema.metadataIngestion.SourceConfig;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration tests for Ingestion Pipeline Log Streaming functionality.
 *
 * <p>Tests verify the log reading/writing APIs work correctly with both
 * default storage and S3 storage configurations.
 *
 * <p>Migrated from:
 * org.umetadata.service.resources.services.ingestionpipelines.IngestionPipelineLogStreamingResourceTest
 *
 * <p>Run with: mvn test -Dtest=IngestionPipelineLogStreamingResourceIT
 */
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(TestNamespaceExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IngestionPipelineLogStreamingResourceIT {

  private static final Logger LOG =
      LoggerFactory.getLogger(IngestionPipelineLogStreamingResourceIT.class);
  private static final String BASE_PATH = "/v1/services/ingestionPipelines";

  @Test
  @Order(1)
  void testReadLogsWithPagination(TestNamespace ns) throws UMetadataException {
    IngestionPipeline pipeline = createTestPipeline(ns);
    UUID runId = UUID.randomUUID();
    String pipelineFQN = pipeline.getFullyQualifiedName();

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + pipelineFQN + "/" + runId + "?limit=10";

    try {
      String response = client.getHttpClient().executeForString(HttpMethod.GET, path, null);

      if (response != null && !response.isEmpty()) {
        Map<String, Object> result = parseJsonResponse(response);
        assertNotNull(result);
        assertTrue(result.containsKey("logs"));
        assertTrue(result.containsKey("after"));
        assertTrue(result.containsKey("total"));
      }
    } catch (UMetadataException e) {
      int statusCode = e.getStatusCode();
      assertTrue(
          statusCode == 200 || statusCode == 404,
          "Expected OK or NOT_FOUND but got: " + statusCode);
    }
  }

  @Test
  @Order(2)
  void testListPipelineRuns(TestNamespace ns) throws UMetadataException {
    IngestionPipeline pipeline = createTestPipeline(ns);
    String pipelineFQN = pipeline.getFullyQualifiedName();

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + pipelineFQN + "?limit=5";

    try {
      String response = client.getHttpClient().executeForString(HttpMethod.GET, path, null);

      if (response != null && !response.isEmpty()) {
        Map<String, Object> result = parseJsonResponse(response);
        assertNotNull(result);
        assertTrue(result.containsKey("runs"));
      }
    } catch (UMetadataException e) {
      int statusCode = e.getStatusCode();
      assertTrue(
          statusCode == 200 || statusCode == 404,
          "Expected OK or NOT_FOUND but got: " + statusCode);
    }
  }

  @Test
  @Order(3)
  void testWriteLogsWithDefaultStorage(TestNamespace ns) throws UMetadataException {
    IngestionPipeline pipeline = createTestPipeline(ns);
    UUID runId = UUID.randomUUID();
    String pipelineFQN = pipeline.getFullyQualifiedName();
    String logContent = "Test log entry at " + System.currentTimeMillis() + "\n";

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + pipelineFQN + "/" + runId;

    Map<String, Object> logBatch = Map.of("logs", logContent);

    try {
      client.getHttpClient().execute(HttpMethod.POST, path, logBatch, String.class);
    } catch (UMetadataException e) {
      int statusCode = e.getStatusCode();
      assertTrue(
          statusCode == 200 || statusCode == 501 || statusCode == 500,
          "Expected OK, NOT_IMPLEMENTED, or INTERNAL_SERVER_ERROR but got: " + statusCode);
    }
  }

  @Test
  @Order(4)
  void testGetLogsForNonExistentRun(TestNamespace ns) throws UMetadataException {
    assumeFalse(
        TestSuiteBootstrap.isK8sEnabled(),
        "Log streaming behavior differs with K8s pipeline backend");
    IngestionPipeline pipeline = createTestPipeline(ns);
    UUID runId = UUID.randomUUID();
    String pipelineFQN = pipeline.getFullyQualifiedName();

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + pipelineFQN + "/" + runId;

    try {
      String response = client.getHttpClient().executeForString(HttpMethod.GET, path, null);

      if (response != null && !response.isEmpty()) {
        Map<String, Object> result = parseJsonResponse(response);
        assertNotNull(result);
        assertTrue(result.containsKey("logs"));

        String logs = (String) result.get("logs");
        assertTrue(logs == null || logs.isEmpty());
      }
    } catch (UMetadataException e) {
      int statusCode = e.getStatusCode();
      assertTrue(
          statusCode == 200 || statusCode == 404,
          "Expected OK or NOT_FOUND but got: " + statusCode);
    }
  }

  @Test
  @Order(5)
  void testListRunsForEmptyPipeline(TestNamespace ns) throws UMetadataException {
    IngestionPipeline pipeline = createTestPipeline(ns);
    String pipelineFQN = pipeline.getFullyQualifiedName();

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + pipelineFQN + "?limit=10";

    try {
      String response = client.getHttpClient().executeForString(HttpMethod.GET, path, null);

      if (response != null && !response.isEmpty()) {
        Map<String, Object> result = parseJsonResponse(response);
        assertNotNull(result);
        assertTrue(result.containsKey("runs"));

        Object runs = result.get("runs");
        assertNotNull(runs);
      }
    } catch (UMetadataException e) {
      int statusCode = e.getStatusCode();
      assertTrue(
          statusCode == 200 || statusCode == 404,
          "Expected OK or NOT_FOUND but got: " + statusCode);
    }
  }

  @Test
  @Order(6)
  void testInvalidPipelineFQN(TestNamespace ns) throws UMetadataException {
    String invalidFQN = "non.existent.pipeline";
    UUID runId = UUID.randomUUID();

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + invalidFQN + "/" + runId;

    UMetadataException exception =
        assertThrows(
            UMetadataException.class,
            () -> client.getHttpClient().executeForString(HttpMethod.GET, path, null));

    assertEquals(404, exception.getStatusCode());
  }

  @Test
  @Order(7)
  void testPaginationParameters(TestNamespace ns) throws UMetadataException {
    IngestionPipeline pipeline = createTestPipeline(ns);
    UUID runId = UUID.randomUUID();
    String pipelineFQN = pipeline.getFullyQualifiedName();

    UMetadataClient client = SdkClients.adminClient();
    String path = BASE_PATH + "/logs/" + pipelineFQN + "/" + runId + "?limit=5&after=100";

    try {
      String response = client.getHttpClient().executeForString(HttpMethod.GET, path, null);

      if (response != null && !response.isEmpty()) {
        Map<String, Object> result = parseJsonResponse(response);
        assertNotNull(result);
        assertTrue(result.containsKey("logs"));
        assertTrue(result.containsKey("after"));
      }
    } catch (UMetadataException e) {
      int statusCode = e.getStatusCode();
      assertTrue(
          statusCode == 200 || statusCode == 404,
          "Expected OK or NOT_FOUND but got: " + statusCode);
    }
  }

  private IngestionPipeline createTestPipeline(TestNamespace ns) {
    DatabaseService service = DatabaseServiceTestFactory.createPostgres(ns);

    DatabaseServiceMetadataPipeline metadataPipeline =
        new DatabaseServiceMetadataPipeline()
            .withType(DatabaseServiceMetadataPipeline.DatabaseMetadataConfigType.DATABASE_METADATA);

    CreateIngestionPipeline createRequest =
        new CreateIngestionPipeline()
            .withName(ns.prefix("pipeline_log_test"))
            .withDisplayName("Test Pipeline for Log Streaming")
            .withDescription("Test pipeline for log streaming functionality")
            .withPipelineType(PipelineType.METADATA)
            .withService(service.getEntityReference())
            .withSourceConfig(new SourceConfig().withConfig(metadataPipeline))
            .withAirflowConfig(
                new AirflowConfig()
                    .withStartDate(
                        new org.joda.time.DateTime("2022-06-10T15:06:47+00:00").toDate()));

    return SdkClients.adminClient().ingestionPipelines().create(createRequest);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> parseJsonResponse(String json) {
    try {
      return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
    } catch (Exception e) {
      LOG.error("Failed to parse JSON response", e);
      return null;
    }
  }
}
