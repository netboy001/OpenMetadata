package org.umetadata.sdk.services.tests;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.UUID;
import org.umetadata.schema.api.tests.CreateTestCaseResolutionStatus;
import org.umetadata.schema.tests.type.Assigned;
import org.umetadata.schema.tests.type.Resolved;
import org.umetadata.schema.tests.type.Severity;
import org.umetadata.schema.tests.type.TestCaseResolutionStatus;
import org.umetadata.schema.tests.type.TestCaseResolutionStatusTypes;
import org.umetadata.schema.utils.ResultList;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;

/**
 * Service for managing test case resolution status (incident management).
 *
 * <p>Resolution status tracks the lifecycle of test case failures, from initial detection through
 * assignment, investigation, and resolution.
 */
public class TestCaseResolutionStatusService {

  private static final String BASE_PATH = "/v1/dataQuality/testCases/testCaseIncidentStatus";
  private final HttpClient httpClient;

  public TestCaseResolutionStatusService(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  // ===================================================================
  // READ OPERATIONS
  // ===================================================================

  /**
   * List all test case resolution statuses.
   *
   * @return List of resolution statuses
   */
  public ListResponse<TestCaseResolutionStatus> list() throws UMetadataException {
    return list(null);
  }

  /**
   * List test case resolution statuses with parameters.
   *
   * @param params Query parameters
   * @return List of resolution statuses
   */
  public ListResponse<TestCaseResolutionStatus> list(ListParams params)
      throws UMetadataException {
    String path = BASE_PATH;
    if (params != null) {
      Map<String, String> queryParams = params.toQueryParams();
      if (!queryParams.isEmpty()) {
        path += "?" + buildQueryString(queryParams);
      }
    }
    ResultList<TestCaseResolutionStatus> result =
        httpClient.execute(HttpMethod.GET, path, null, getResultListType());
    return new ListResponse<>(result);
  }

  /**
   * Get a resolution status by state ID.
   *
   * @param stateId The state ID
   * @return The resolution status
   */
  public TestCaseResolutionStatus getByStateId(UUID stateId) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.GET, BASE_PATH + "/stateId/" + stateId, null, TestCaseResolutionStatus.class);
  }

  /**
   * Get a resolution status by ID.
   *
   * @param id The resolution status ID
   * @return The resolution status
   */
  public TestCaseResolutionStatus get(UUID id) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.GET, BASE_PATH + "/" + id, null, TestCaseResolutionStatus.class);
  }

  /**
   * Search resolution statuses.
   *
   * @param params Search parameters
   * @return List of matching resolution statuses
   */
  public ListResponse<TestCaseResolutionStatus> searchList(ListParams params)
      throws UMetadataException {
    String path = BASE_PATH + "/search/list";
    if (params != null) {
      Map<String, String> queryParams = params.toQueryParams();
      if (!queryParams.isEmpty()) {
        path += "?" + buildQueryString(queryParams);
      }
    }
    ResultList<TestCaseResolutionStatus> result =
        httpClient.execute(HttpMethod.GET, path, null, getResultListType());
    return new ListResponse<>(result);
  }

  // ===================================================================
  // CREATE OPERATIONS
  // ===================================================================

  /**
   * Create a new resolution status.
   *
   * @param request The creation request
   * @return The created resolution status
   */
  public TestCaseResolutionStatus create(CreateTestCaseResolutionStatus request)
      throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, BASE_PATH, request, TestCaseResolutionStatus.class);
  }

  // ===================================================================
  // UPDATE OPERATIONS
  // ===================================================================

  /**
   * Patch a resolution status.
   *
   * @param id The resolution status ID
   * @param patch The JSON patch to apply
   * @return The updated resolution status
   */
  public TestCaseResolutionStatus patch(UUID id, JsonNode patch) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.PATCH, BASE_PATH + "/" + id, patch, TestCaseResolutionStatus.class);
  }

  // ===================================================================
  // HELPER METHODS
  // ===================================================================

  private String buildQueryString(Map<String, String> params) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, String> entry : params.entrySet()) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      sb.append(entry.getKey()).append("=").append(entry.getValue());
    }
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  private Class<ResultList<TestCaseResolutionStatus>> getResultListType() {
    return (Class<ResultList<TestCaseResolutionStatus>>) (Class<?>) ResultList.class;
  }

  // ===================================================================
  // FLUENT API BUILDER
  // ===================================================================

  /** Builder for creating resolution status with a fluent API. */
  public static class StatusBuilder {
    private final TestCaseResolutionStatusService service;
    private TestCaseResolutionStatusTypes statusType;
    private String testCaseReferenceFqn;
    private Object resolutionDetails;
    private Severity severity;

    public StatusBuilder(TestCaseResolutionStatusService service) {
      this.service = service;
    }

    public StatusBuilder statusType(TestCaseResolutionStatusTypes type) {
      this.statusType = type;
      return this;
    }

    public StatusBuilder newIncident() {
      this.statusType = TestCaseResolutionStatusTypes.New;
      return this;
    }

    public StatusBuilder ack() {
      this.statusType = TestCaseResolutionStatusTypes.Ack;
      return this;
    }

    public StatusBuilder assigned() {
      this.statusType = TestCaseResolutionStatusTypes.Assigned;
      return this;
    }

    public StatusBuilder resolved() {
      this.statusType = TestCaseResolutionStatusTypes.Resolved;
      return this;
    }

    public StatusBuilder testCaseReference(String fqn) {
      this.testCaseReferenceFqn = fqn;
      return this;
    }

    public StatusBuilder resolvedDetails(Resolved details) {
      this.resolutionDetails = details;
      return this;
    }

    public StatusBuilder assignedDetails(Assigned details) {
      this.resolutionDetails = details;
      return this;
    }

    public StatusBuilder severity(Severity severity) {
      this.severity = severity;
      return this;
    }

    public CreateTestCaseResolutionStatus build() {
      CreateTestCaseResolutionStatus request = new CreateTestCaseResolutionStatus();
      request.setTestCaseResolutionStatusType(statusType);
      request.setTestCaseReference(testCaseReferenceFqn);
      if (resolutionDetails != null) {
        request.setTestCaseResolutionStatusDetails(resolutionDetails);
      }
      if (severity != null) {
        request.setSeverity(severity);
      }
      return request;
    }

    public TestCaseResolutionStatus create() throws UMetadataException {
      return service.create(build());
    }
  }

  /**
   * Create a fluent builder for creating a resolution status.
   *
   * @return A status builder
   */
  public StatusBuilder builder() {
    return new StatusBuilder(this);
  }
}
