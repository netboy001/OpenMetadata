package org.umetadata.service.mapper;

import org.umetadata.schema.EntityInterface;

public interface EntityUpdateMapper<T extends EntityInterface, U> {
  String getFqn(U update);

  void applyEntitySpecificUpdates(T original, U update);
}
