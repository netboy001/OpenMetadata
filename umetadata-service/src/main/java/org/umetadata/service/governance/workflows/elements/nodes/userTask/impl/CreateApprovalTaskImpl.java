package org.umetadata.service.governance.workflows.elements.nodes.userTask.impl;

import static org.umetadata.service.governance.workflows.Workflow.EXCEPTION_VARIABLE;
import static org.umetadata.service.governance.workflows.Workflow.RELATED_ENTITY_VARIABLE;
import static org.umetadata.service.governance.workflows.Workflow.WORKFLOW_RUNTIME_EXCEPTION;
import static org.umetadata.service.governance.workflows.WorkflowHandler.getProcessDefinitionKeyFromId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.entity.feed.Thread;
import org.umetadata.schema.type.ChangeEvent;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.EventType;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.type.TaskDetails;
import org.umetadata.schema.type.TaskStatus;
import org.umetadata.schema.type.TaskType;
import org.umetadata.schema.type.ThreadType;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.exception.EntityNotFoundException;
import org.umetadata.service.governance.workflows.WorkflowHandler;
import org.umetadata.service.governance.workflows.WorkflowVariableHandler;
import org.umetadata.service.governance.workflows.util.ChangePreviewUtils;
import org.umetadata.service.jdbi3.FeedRepository;
import org.umetadata.service.resources.feeds.FeedMapper;
import org.umetadata.service.resources.feeds.MessageParser;
import org.umetadata.service.util.WebsocketNotificationHandler;

@Slf4j
public class CreateApprovalTaskImpl implements TaskListener {

  private Expression inputNamespaceMapExpr;
  private Expression approvalThresholdExpr;
  private Expression rejectionThresholdExpr;

  @Override
  public void notify(DelegateTask delegateTask) {
    WorkflowVariableHandler varHandler = new WorkflowVariableHandler(delegateTask);
    try {
      Map<String, String> inputNamespaceMap =
          JsonUtils.readOrConvertValue(inputNamespaceMapExpr.getValue(delegateTask), Map.class);
      List<EntityReference> assignees = getAssignees(delegateTask);
      MessageParser.EntityLink entityLink =
          MessageParser.EntityLink.parse(
              (String)
                  varHandler.getNamespacedVariable(
                      inputNamespaceMap.get(RELATED_ENTITY_VARIABLE), RELATED_ENTITY_VARIABLE));
      EntityInterface entity = Entity.getEntity(entityLink, "*", Include.ALL);

      int approvalThreshold = resolveThreshold(approvalThresholdExpr, delegateTask, 1);
      int rejectionThreshold = resolveThreshold(rejectionThresholdExpr, delegateTask, 1);

      Thread task = createApprovalTask(entity, assignees);
      WorkflowHandler.getInstance().setCustomTaskId(delegateTask.getId(), task.getId());

      delegateTask.setVariable("approvalThreshold", approvalThreshold);
      delegateTask.setVariable("rejectionThreshold", rejectionThreshold);
      delegateTask.setVariable("approversList", new ArrayList<String>());
      delegateTask.setVariable("rejectersList", new ArrayList<String>());
    } catch (Exception exc) {
      LOG.error(
          String.format(
              "[%s] Failure: ",
              getProcessDefinitionKeyFromId(delegateTask.getProcessDefinitionId())),
          exc);
      varHandler.setGlobalVariable(EXCEPTION_VARIABLE, ExceptionUtils.getStackTrace(exc));
      throw new BpmnError(WORKFLOW_RUNTIME_EXCEPTION, exc.getMessage());
    }
  }

  private static int resolveThreshold(
      Expression expr, DelegateTask delegateTask, int defaultValue) {
    if (expr == null) return defaultValue;
    String raw = (String) expr.getValue(delegateTask);
    if (raw == null || raw.trim().isEmpty()) return defaultValue;
    try {
      return Integer.parseInt(raw.trim());
    } catch (NumberFormatException exc) {
      LOG.warn(
          "Invalid threshold value '{}' resolved from workflow expression. Falling back to default value {}.",
          raw,
          defaultValue);
      return defaultValue;
    }
  }

  private List<EntityReference> getAssignees(DelegateTask delegateTask) {
    List<EntityReference> assignees = new ArrayList<>();
    Set<IdentityLink> candidates = delegateTask.getCandidates();
    if (!candidates.isEmpty()) {
      for (IdentityLink candidate : candidates) {
        assignees.add(getEntityReferenceFromLinkString(candidate.getUserId()));
      }
    } else {
      assignees.add(getEntityReferenceFromLinkString(delegateTask.getAssignee()));
    }
    return assignees;
  }

  private EntityReference getEntityReferenceFromLinkString(String entityLinkString) {
    MessageParser.EntityLink assigneeEntityLink = MessageParser.EntityLink.parse(entityLinkString);
    return Entity.getEntityReferenceByName(
        assigneeEntityLink.getEntityType(), assigneeEntityLink.getEntityFQN(), Include.NON_DELETED);
  }

  private Thread createApprovalTask(EntityInterface entity, List<EntityReference> assignees) {
    FeedRepository feedRepository = Entity.getFeedRepository();
    MessageParser.EntityLink about =
        new MessageParser.EntityLink(
            Entity.getEntityTypeFromObject(entity), entity.getFullyQualifiedName());

    Thread thread;
    ChangeEvent changeEvent;
    try {
      thread = feedRepository.getTask(about, TaskType.RequestApproval, TaskStatus.Open);
      thread.getTask().setAssignees(FeedMapper.formatAssignees(assignees));
      ChangePreviewUtils.applyChangePreview(thread, entity, thread.getMessage());
      thread.withUpdatedBy(entity.getUpdatedBy()).withUpdatedAt(System.currentTimeMillis());

      Entity.getCollectionDAO().feedDAO().update(thread.getId(), JsonUtils.pojoToJson(thread));

      WorkflowHandler.getInstance()
          .terminateTaskProcessInstance(thread.getId(), "A Newer Process Instance is Running.");
      changeEvent =
          new ChangeEvent()
              .withId(UUID.randomUUID())
              .withEventType(EventType.THREAD_UPDATED)
              .withEntityId(thread.getId())
              .withEntityType(Entity.THREAD)
              .withUserName(entity.getUpdatedBy())
              .withTimestamp(thread.getUpdatedAt())
              .withEntity(thread);
    } catch (EntityNotFoundException ex) {
      TaskDetails taskDetails =
          new TaskDetails()
              .withAssignees(FeedMapper.formatAssignees(assignees))
              .withType(TaskType.RequestApproval)
              .withStatus(TaskStatus.Open);

      thread =
          new Thread()
              .withId(UUID.randomUUID())
              .withThreadTs(System.currentTimeMillis())
              .withCreatedBy(entity.getUpdatedBy())
              .withAbout(about.getLinkString())
              .withType(ThreadType.Task)
              .withTask(taskDetails)
              .withDomains(
                  entity.getDomains() == null
                      ? Collections.emptyList()
                      : entity.getDomains().stream().map(EntityReference::getId).toList())
              .withUpdatedBy(entity.getUpdatedBy())
              .withUpdatedAt(System.currentTimeMillis());
      ChangePreviewUtils.applyChangePreview(thread, entity, null);
      feedRepository.create(thread);

      changeEvent =
          new ChangeEvent()
              .withId(UUID.randomUUID())
              .withEventType(EventType.THREAD_CREATED)
              .withEntityId(thread.getId())
              .withEntityType(Entity.THREAD)
              .withUserName(entity.getUpdatedBy())
              .withTimestamp(thread.getUpdatedAt())
              .withEntity(thread);
    }
    Entity.getCollectionDAO().changeEventDAO().insert(JsonUtils.pojoToMaskedJson(changeEvent));
    WebsocketNotificationHandler.handleTaskNotification(thread);
    return thread;
  }
}
