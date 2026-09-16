package org.umetadata.service.resources.tags;

import static org.umetadata.service.Entity.CLASSIFICATION;
import static org.umetadata.service.Entity.TAG;
import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.classification.CreateTag;
import org.umetadata.schema.entity.classification.Tag;
import org.umetadata.service.mapper.EntityMapper;

public class TagMapper implements EntityMapper<Tag, CreateTag> {
  @Override
  public Tag createToEntity(CreateTag create, String user) {
    return copy(new Tag(), create, user)
        .withStyle(create.getStyle())
        .withParent(getEntityReference(TAG, create.getParent()))
        .withClassification(getEntityReference(CLASSIFICATION, create.getClassification()))
        .withProvider(create.getProvider())
        .withMutuallyExclusive(create.getMutuallyExclusive())
        .withRecognizers(create.getRecognizers())
        .withAutoClassificationEnabled(create.getAutoClassificationEnabled())
        .withAutoClassificationPriority(create.getAutoClassificationPriority());
  }
}
