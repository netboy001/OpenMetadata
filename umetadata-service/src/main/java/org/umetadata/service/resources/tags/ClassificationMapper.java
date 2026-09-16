package org.umetadata.service.resources.tags;

import org.umetadata.schema.api.classification.CreateClassification;
import org.umetadata.schema.entity.classification.AutoClassificationConfig;
import org.umetadata.schema.entity.classification.Classification;
import org.umetadata.service.mapper.EntityMapper;

public class ClassificationMapper implements EntityMapper<Classification, CreateClassification> {
  @Override
  public Classification createToEntity(CreateClassification create, String user) {
    Classification classification =
        copy(new Classification(), create, user)
            .withFullyQualifiedName(create.getName())
            .withProvider(create.getProvider())
            .withMutuallyExclusive(create.getMutuallyExclusive());

    // Copy autoClassificationConfig if present - need to convert from API to entity version
    if (create.getAutoClassificationConfig() != null) {
      org.umetadata.schema.api.classification.AutoClassificationConfig apiConfig =
          create.getAutoClassificationConfig();
      AutoClassificationConfig entityConfig =
          new AutoClassificationConfig()
              .withEnabled(apiConfig.getEnabled())
              .withMinimumConfidence(apiConfig.getMinimumConfidence())
              .withRequireExplicitMatch(apiConfig.getRequireExplicitMatch());

      // Convert ConflictResolution enum
      if (apiConfig.getConflictResolution() != null) {
        entityConfig.withConflictResolution(
            AutoClassificationConfig.ConflictResolution.fromValue(
                apiConfig.getConflictResolution().value()));
      }

      classification.withAutoClassificationConfig(entityConfig);
    }

    return classification;
  }
}
