package org.umetadata.sdk.services.automations;

import org.umetadata.schema.entity.automations.CreateWorkflow;
import org.umetadata.schema.entity.automations.Workflow;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class WorkflowService extends EntityServiceBase<Workflow> {

  public WorkflowService(HttpClient httpClient) {
    super(httpClient, "/v1/automations/workflows");
  }

  @Override
  protected Class<Workflow> getEntityClass() {
    return Workflow.class;
  }

  public Workflow create(CreateWorkflow request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Workflow.class);
  }
}
