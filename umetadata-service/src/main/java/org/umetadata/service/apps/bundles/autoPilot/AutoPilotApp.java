package org.umetadata.service.apps.bundles.autoPilot;

import static org.umetadata.service.governance.workflows.Workflow.GLOBAL_NAMESPACE;
import static org.umetadata.service.governance.workflows.Workflow.RELATED_ENTITY_VARIABLE;
import static org.umetadata.service.governance.workflows.WorkflowVariableHandler.getNamespacedVariableName;
import static org.umetadata.service.governance.workflows.elements.TriggerFactory.getTriggerWorkflowId;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.entity.app.App;
import org.umetadata.schema.entity.app.internal.AutoPilotAppConfig;
import org.umetadata.schema.governance.workflows.WorkflowDefinition;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.apps.AbstractNativeApplication;
import org.umetadata.service.exception.UnhandledServerException;
import org.umetadata.service.governance.workflows.WorkflowHandler;
import org.umetadata.service.jdbi3.CollectionDAO;
import org.umetadata.service.jdbi3.UserRepository;
import org.umetadata.service.jdbi3.WorkflowDefinitionRepository;
import org.umetadata.service.search.SearchRepository;

@Slf4j
public class AutoPilotApp extends AbstractNativeApplication {
  private static final String WORKFLOW_NAME = "AutoPilotWorkflow";
  protected AutoPilotAppConfig config;

  public AutoPilotApp(CollectionDAO collectionDAO, SearchRepository searchRepository) {
    super(collectionDAO, searchRepository);
  }

  @Override
  public void install(String installedBy) {
    createWorkflow(installedBy);
    configure();
  }

  @Override
  public void uninstall() {
    super.uninstall();
    deleteWorkflow();
  }

  @Override
  public void init(App app) {
    super.init(app);
    this.config =
        JsonUtils.convertValue(this.getApp().getAppConfiguration(), AutoPilotAppConfig.class);
  }

  @Override
  public void configure() {
    if (this.config.getActive()) {
      resumeWorkflow();
    } else {
      suspendWorkflow();
    }
  }

  @Override
  public void triggerOnDemand(Map<String, Object> config) {
    // Trigger the application with the provided configuration payload
    Map<String, Object> appConfig = JsonUtils.getMap(getApp().getAppConfiguration());
    if (config != null) {
      appConfig.putAll(config);
    }
    validateConfig(appConfig);

    AutoPilotAppConfig runtimeConfig =
        JsonUtils.readOrConvertValue(appConfig, AutoPilotAppConfig.class);

    if (runtimeConfig.getActive()) {
      Map<String, Object> variables = new HashMap<>();
      variables.put(
          getNamespacedVariableName(GLOBAL_NAMESPACE, RELATED_ENTITY_VARIABLE),
          runtimeConfig.getEntityLink());

      WorkflowHandler.getInstance()
          .triggerByKey(
              getTriggerWorkflowId(WORKFLOW_NAME), UUID.randomUUID().toString(), variables);
    } else {
      LOG.info(
          String.format(
              "%s is not active. Won't be triggered for %s",
              WORKFLOW_NAME, runtimeConfig.getEntityLink()));
    }
  }

  private String readResource(String resourceFile) {
    try (InputStream in = getClass().getResourceAsStream(resourceFile)) {
      assert in != null;
      return new String(in.readAllBytes());
    } catch (Exception e) {
      throw new UnhandledServerException("Failed to load AutoPilot Workflow.");
    }
  }

  private boolean resourceExists(String resourceFile) {
    return getClass().getResource(resourceFile) != null;
  }

  private String getAppBot() {
    return getApp().getBot().getName();
  }

  private WorkflowDefinition loadWorkflow() {
    UserRepository userRepository = (UserRepository) Entity.getEntityRepository(Entity.USER);
    EntityReference adminReference =
        userRepository.findByName(getAppBot(), Include.NON_DELETED).getEntityReference();

    String resourceFile = "/applications/AutoPilotApplication/collate/AutoPilotWorkflow.json";
    resourceFile =
        resourceExists(resourceFile)
            ? resourceFile
            : resourceFile.replace("collate", "umetadata");

    return JsonUtils.readOrConvertValue(readResource(resourceFile), WorkflowDefinition.class)
        .withOwners(List.of(adminReference))
        .withUpdatedAt(System.currentTimeMillis())
        .withUpdatedBy(getAppBot());
  }

  private void createWorkflow(String createdBy) {
    WorkflowDefinitionRepository repository =
        (WorkflowDefinitionRepository) Entity.getEntityRepository(Entity.WORKFLOW_DEFINITION);
    repository.createOrUpdate(null, loadWorkflow(), createdBy);
  }

  private void deleteWorkflow() {
    WorkflowDefinitionRepository repository =
        (WorkflowDefinitionRepository) Entity.getEntityRepository(Entity.WORKFLOW_DEFINITION);
    repository.deleteByName(getAppBot(), WORKFLOW_NAME, true, true);
  }

  private void suspendWorkflow() {
    WorkflowHandler.getInstance().suspendWorkflow(WORKFLOW_NAME);
  }

  private void resumeWorkflow() {
    WorkflowHandler.getInstance().resumeWorkflow(WORKFLOW_NAME);
  }
}
