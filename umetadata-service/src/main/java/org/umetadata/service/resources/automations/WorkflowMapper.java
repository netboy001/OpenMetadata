package org.umetadata.service.resources.automations;

import org.umetadata.schema.entity.automations.CreateWorkflow;
import org.umetadata.schema.entity.automations.Workflow;
import org.umetadata.service.mapper.EntityMapper;

public class WorkflowMapper implements EntityMapper<Workflow, CreateWorkflow> {

  @Override
  public Workflow createToEntity(CreateWorkflow create, String user) {
    return copy(new Workflow(), create, user)
        .withDescription(create.getDescription())
        .withRequest(create.getRequest())
        .withWorkflowType(create.getWorkflowType())
        .withDisplayName(create.getDisplayName())
        .withResponse(create.getResponse())
        .withStatus(create.getStatus())
        .withName(create.getName());
  }
}
