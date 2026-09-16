package org.umetadata.service.security.policyevaluator;

import java.util.ArrayList;
import java.util.List;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.type.TagLabel;
import org.umetadata.service.Entity;

/** Conversation threads require special handling */
public record ThreadResourceContext(String createdBy) implements ResourceContextInterface {
  @Override
  public String getResource() {
    return Entity.THREAD;
  }

  @Override
  public List<EntityReference> getOwners() {
    List<EntityReference> owners = new ArrayList<>();
    owners.add(Entity.getEntityReferenceByName(Entity.USER, createdBy, Include.NON_DELETED));
    return owners;
  }

  @Override
  public List<TagLabel> getTags() {
    return null;
  }

  @Override
  public EntityInterface getEntity() {
    return null;
  }

  // TODO: Fix this this should be thread.getEntity().getDomain()
  @Override
  public List<EntityReference> getDomains() {
    return null;
  }
}
