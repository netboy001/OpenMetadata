package org.umetadata.service.util;

import java.util.List;
import org.umetadata.schema.entity.data.EntityHierarchy;
import org.umetadata.schema.utils.ResultList;

public class EntityHierarchyList extends ResultList<EntityHierarchy> {
  @SuppressWarnings("unused")
  public EntityHierarchyList() {}

  public EntityHierarchyList(List<EntityHierarchy> data) {
    super(data, null, null, data.size());
  }
}
