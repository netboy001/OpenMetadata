package org.umetadata.service.governance.workflows.elements;

import org.umetadata.schema.governance.workflows.WorkflowConfiguration;
import org.umetadata.schema.governance.workflows.elements.NodeSubType;
import org.umetadata.schema.governance.workflows.elements.WorkflowNodeDefinitionInterface;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.ApplyRecognizerFeedbackTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.CheckChangeDescriptionTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.CheckEntityAttributesTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.CreateAndRunIngestionPipelineTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.DataCompletenessTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.RejectRecognizerFeedbackTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.RollbackEntityTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.RunAppTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.SetEntityAttributeTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.SetEntityCertificationTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.SetGlossaryTermStatusTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.automatedTask.SinkTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.endEvent.EndEventDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.gateway.ParallelGatewayDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.startEvent.StartEventDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.userTask.CreateRecognizerFeedbackApprovalTaskDefinition;
import org.umetadata.schema.governance.workflows.elements.nodes.userTask.UserApprovalTaskDefinition;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.ApplyRecognizerFeedbackTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.CheckChangeDescriptionTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.CheckEntityAttributesTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.DataCompletenessTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.RejectRecognizerFeedbackTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.RollbackEntityTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.SetEntityAttributeTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.SetEntityCertificationTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.SetGlossaryTermStatusTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.SinkTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.createAndRunIngestionPipeline.CreateAndRunIngestionPipelineTask;
import org.umetadata.service.governance.workflows.elements.nodes.automatedTask.runApp.RunAppTask;
import org.umetadata.service.governance.workflows.elements.nodes.endEvent.EndEvent;
import org.umetadata.service.governance.workflows.elements.nodes.gateway.ParallelGateway;
import org.umetadata.service.governance.workflows.elements.nodes.startEvent.StartEvent;
import org.umetadata.service.governance.workflows.elements.nodes.userTask.CreateRecognizerFeedbackApprovalTask;
import org.umetadata.service.governance.workflows.elements.nodes.userTask.UserApprovalTask;

public class NodeFactory {
  public static NodeInterface createNode(
      WorkflowNodeDefinitionInterface nodeDefinition, WorkflowConfiguration config) {
    return switch (NodeSubType.fromValue(nodeDefinition.getSubType())) {
      case START_EVENT -> new StartEvent((StartEventDefinition) nodeDefinition, config);
      case END_EVENT -> new EndEvent((EndEventDefinition) nodeDefinition, config);
      case CHECK_ENTITY_ATTRIBUTES_TASK -> new CheckEntityAttributesTask(
          (CheckEntityAttributesTaskDefinition) nodeDefinition, config);
      case CHECK_CHANGE_DESCRIPTION_TASK -> new CheckChangeDescriptionTask(
          (CheckChangeDescriptionTaskDefinition) nodeDefinition, config);
      case SET_ENTITY_ATTRIBUTE_TASK -> new SetEntityAttributeTask(
          (SetEntityAttributeTaskDefinition) nodeDefinition, config);
      case SET_ENTITY_CERTIFICATION_TASK -> new SetEntityCertificationTask(
          (SetEntityCertificationTaskDefinition) nodeDefinition, config);
      case SET_GLOSSARY_TERM_STATUS_TASK -> new SetGlossaryTermStatusTask(
          (SetGlossaryTermStatusTaskDefinition) nodeDefinition, config);
      case USER_APPROVAL_TASK -> new UserApprovalTask(
          (UserApprovalTaskDefinition) nodeDefinition, config);
      case CREATE_AND_RUN_INGESTION_PIPELINE_TASK -> new CreateAndRunIngestionPipelineTask(
          (CreateAndRunIngestionPipelineTaskDefinition) nodeDefinition, config);
      case RUN_APP_TASK -> new RunAppTask((RunAppTaskDefinition) nodeDefinition, config);
      case ROLLBACK_ENTITY_TASK -> new RollbackEntityTask(
          (RollbackEntityTaskDefinition) nodeDefinition, config);
      case DATA_COMPLETENESS_TASK -> new DataCompletenessTask(
          (DataCompletenessTaskDefinition) nodeDefinition, config);
      case PARALLEL_GATEWAY -> new ParallelGateway(
          (ParallelGatewayDefinition) nodeDefinition, config);
      case SINK_TASK -> new SinkTask((SinkTaskDefinition) nodeDefinition, config);
      case CREATE_RECOGNIZER_FEEDBACK_APPROVAL_TASK -> new CreateRecognizerFeedbackApprovalTask(
          (CreateRecognizerFeedbackApprovalTaskDefinition) nodeDefinition, config);
      case APPLY_RECOGNIZER_FEEDBACK_TASK -> new ApplyRecognizerFeedbackTask(
          (ApplyRecognizerFeedbackTaskDefinition) nodeDefinition, config);
      case REJECT_RECOGNIZER_FEEDBACK_TASK -> new RejectRecognizerFeedbackTask(
          (RejectRecognizerFeedbackTaskDefinition) nodeDefinition, config);
      default -> throw new IllegalArgumentException(
          "Unsupported node subtype: " + nodeDefinition.getSubType());
    };
  }
}
