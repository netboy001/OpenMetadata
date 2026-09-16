package org.umetadata.sdk.services.governance;

import java.util.Map;
import org.umetadata.schema.api.governance.CreateWorkflowDefinition;
import org.umetadata.schema.governance.workflows.WorkflowDefinition;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.network.RequestOptions;
import org.umetadata.sdk.services.EntityServiceBase;

public class WorkflowDefinitionService extends EntityServiceBase<WorkflowDefinition> {

  public WorkflowDefinitionService(HttpClient httpClient) {
    super(httpClient, "/v1/governance/workflowDefinitions");
  }

  @Override
  protected Class<WorkflowDefinition> getEntityClass() {
    return WorkflowDefinition.class;
  }

  public WorkflowDefinition create(CreateWorkflowDefinition request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, WorkflowDefinition.class);
  }

  public WorkflowDefinition upsert(CreateWorkflowDefinition request) throws UMetadataException {
    return httpClient.execute(HttpMethod.PUT, basePath, request, WorkflowDefinition.class);
  }

  public void trigger(String name) throws UMetadataException {
    httpClient.executeForString(
        HttpMethod.POST,
        basePath + "/name/" + name + "/trigger",
        Map.of(),
        RequestOptions.builder().build());
  }

  public WorkflowDefinition suspend(String name) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.PUT,
        basePath + "/name/" + name + "/suspend",
        Map.of(),
        WorkflowDefinition.class);
  }

  public WorkflowDefinition resume(String name) throws UMetadataException {
    return httpClient.execute(
        HttpMethod.PUT, basePath + "/name/" + name + "/resume", Map.of(), WorkflowDefinition.class);
  }

  public WorkflowDefinition validate(CreateWorkflowDefinition request)
      throws UMetadataException {
    return httpClient.execute(
        HttpMethod.POST, basePath + "/validate", request, WorkflowDefinition.class);
  }
}
