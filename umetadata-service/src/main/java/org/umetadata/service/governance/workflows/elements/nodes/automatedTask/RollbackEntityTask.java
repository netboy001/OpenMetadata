package org.umetadata.service.governance.workflows.elements.nodes.automatedTask;

import static org.umetadata.service.governance.workflows.Workflow.getFlowableElementId;

import java.util.HashMap;
import lombok.Getter;
import org.flowable.bpmn.model.BoundaryEvent;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FieldExtension;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.ServiceTask;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.SubProcess;
import org.umetadata.schema.governance.workflows.WorkflowConfiguration;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.RollbackEntityTaskDefinition;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.governance.workflows.elements.NodeInterface;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.impl.RollbackEntityImpl;
import org.umetadata.service.governance.workflows.flowable.builders.EndEventBuilder;
import org.umetadata.service.governance.workflows.flowable.builders.FieldExtensionBuilder;
import org.umetadata.service.governance.workflows.flowable.builders.ServiceTaskBuilder;
import org.umetadata.service.governance.workflows.flowable.builders.StartEventBuilder;
import org.umetadata.service.governance.workflows.flowable.builders.SubProcessBuilder;

@Getter
public class RollbackEntityTask implements NodeInterface {
  private final SubProcess subProcess;
  private final BoundaryEvent runtimeExceptionBoundaryEvent;

  public RollbackEntityTask(
      RollbackEntityTaskDefinition nodeDefinition, WorkflowConfiguration config) {
    String subProcessId = nodeDefinition.getName();

    SubProcess subProcess = new SubProcessBuilder().id(subProcessId).build();

    StartEvent startEvent =
        new StartEventBuilder().id(getFlowableElementId(subProcessId, "startEvent")).build();

    ServiceTask rollbackEntityTask = getRollbackEntityServiceTask(subProcessId, nodeDefinition);

    EndEvent endEvent =
        new EndEventBuilder().id(getFlowableElementId(subProcessId, "endEvent")).build();

    subProcess.addFlowElement(startEvent);
    subProcess.addFlowElement(rollbackEntityTask);
    subProcess.addFlowElement(endEvent);

    subProcess.addFlowElement(new SequenceFlow(startEvent.getId(), rollbackEntityTask.getId()));
    subProcess.addFlowElement(new SequenceFlow(rollbackEntityTask.getId(), endEvent.getId()));

    if (config.getStoreStageStatus()) {
      attachWorkflowInstanceStageListeners(subProcess);
    }

    this.subProcess = subProcess;
    this.runtimeExceptionBoundaryEvent = getRuntimeExceptionBoundaryEvent(subProcess, false);
  }

  private ServiceTask getRollbackEntityServiceTask(
      String subProcessId, RollbackEntityTaskDefinition nodeDefinition) {

    // Pass the input namespace map so RollbackEntityImpl can access namespaced variables
    FieldExtension inputNamespaceMapExpr =
        new FieldExtensionBuilder()
            .fieldName("inputNamespaceMapExpr")
            .fieldValue(
                JsonUtils.pojoToJson(
                    nodeDefinition.getInputNamespaceMap() != null
                        ? nodeDefinition.getInputNamespaceMap()
                        : new HashMap<>()))
            .build();

    return new ServiceTaskBuilder()
        .id(getFlowableElementId(subProcessId, "rollbackEntity"))
        .implementation(RollbackEntityImpl.class.getName())
        .addFieldExtension(inputNamespaceMapExpr)
        .build();
  }

  @Override
  public void addToWorkflow(BpmnModel model, Process process) {
    process.addFlowElement(subProcess);
    process.addFlowElement(runtimeExceptionBoundaryEvent);
  }

  @Override
  public BoundaryEvent getRuntimeExceptionBoundaryEvent() {
    return runtimeExceptionBoundaryEvent;
  }
}
