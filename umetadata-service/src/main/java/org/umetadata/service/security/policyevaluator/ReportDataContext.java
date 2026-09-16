package org.umetadata.service.security.policyevaluator;

import java.util.List;
import lombok.Builder;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.TagLabel;
import org.umetadata.service.Entity;

@Builder
public class ReportDataContext implements ResourceContextInterface {
  @Override
  public String getResource() {
    return Entity.DATA_INSIGHT_CHART;
  }

  @Override
  public List<EntityReference> getOwners() {
    return null;
  }

  @Override
  public List<TagLabel> getTags() {
    return null;
  }

  @Override
  public EntityInterface getEntity() {
    return null;
  }

  @Override
  public List<EntityReference> getDomains() {
    return null;
  }
}
