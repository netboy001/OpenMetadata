package org.umetadata.service.jdbi3;

import static org.umetadata.service.Entity.WORKFLOW;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.umetadata.schema.entity.automations.Workflow;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.schema.type.change.ChangeSource;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.automations.WorkflowResource;
import org.umetadata.service.secrets.SecretsManager;
import org.umetadata.service.secrets.SecretsManagerFactory;
import org.umetadata.service.util.EntityUtil;
import org.umetadata.service.util.EntityUtil.RelationIncludes;

public class WorkflowRepository extends EntityRepository<Workflow> {
  private static final String PATCH_FIELDS = "status,response";

  public WorkflowRepository() {
    super(
        WorkflowResource.COLLECTION_PATH,
        WORKFLOW,
        Workflow.class,
        Entity.getCollectionDAO().workflowDAO(),
        PATCH_FIELDS,
        "");
    quoteFqn = true;
  }

  @Override
  public void setFields(
      Workflow entity, EntityUtil.Fields fields, RelationIncludes relationIncludes) {
    /* Nothing to do */
  }

  @Override
  public void clearFields(Workflow entity, EntityUtil.Fields fields) {
    /* Nothing to do */
  }

  @Override
  public void prepare(Workflow entity, boolean update) {
    // validate request and status
    if (entity.getRequest() == null) {
      throw new IllegalArgumentException("Request must not be empty");
    }
  }

  @Override
  public void storeEntity(Workflow entity, boolean update) {
    UMetadataConnection umetadataConnection = entity.getuMetadataServerConnection();
    SecretsManager secretsManager = SecretsManagerFactory.getSecretsManager();

    if (secretsManager != null) {
      entity = secretsManager.encryptWorkflow(entity);
    }

    // Don't store owners, database, href and tags as JSON. Build it on the fly based on
    // relationships
    entity.withuMetadataServerConnection(null);
    store(entity, update);

    // Restore the relationships
    entity.withuMetadataServerConnection(umetadataConnection);
  }

  public void storeEntities(List<Workflow> workflows) {
    List<Workflow> workflowsToStore = new ArrayList<>();
    Gson gson = new Gson();
    SecretsManager secretsManager = SecretsManagerFactory.getSecretsManager();

    for (Workflow workflow : workflows) {
      UMetadataConnection umetadataConnection = workflow.getuMetadataServerConnection();

      if (secretsManager != null) {
        workflow = secretsManager.encryptWorkflow(workflow);
      }

      workflow.withuMetadataServerConnection(null);

      String jsonCopy = gson.toJson(workflow);
      workflowsToStore.add(gson.fromJson(jsonCopy, Workflow.class));

      workflow.withuMetadataServerConnection(umetadataConnection);
    }

    storeMany(workflowsToStore);
  }

  /** Remove the secrets from the secret manager */
  @Override
  protected void postDelete(Workflow workflow, boolean hardDelete) {
    super.postDelete(workflow, hardDelete);
    SecretsManagerFactory.getSecretsManager().deleteSecretsFromWorkflow(workflow);
  }

  @Override
  public void storeRelationships(Workflow entity) {
    // No relationships to store beyond what is stored in the super class
  }

  @Override
  public EntityRepository<Workflow>.EntityUpdater getUpdater(
      Workflow original, Workflow updated, Operation operation, ChangeSource changeSource) {
    return new WorkflowUpdater(original, updated, operation);
  }

  public class WorkflowUpdater extends EntityUpdater {
    public WorkflowUpdater(Workflow original, Workflow updated, Operation operation) {
      super(original, updated, operation);
    }

    @Transaction
    @Override
    public void entitySpecificUpdate(boolean consolidatingChanges) {
      recordChange("status", original.getStatus(), updated.getStatus());
      recordChange("response", original.getResponse(), updated.getResponse(), true);
    }
  }
}
