package org.umetadata.service.migration.mysql.v160;

import static org.umetadata.service.migration.utils.v160.MigrationUtil.addDisplayNameToCustomProperty;
import static org.umetadata.service.migration.utils.v160.MigrationUtil.addEditGlossaryTermsToDataConsumerPolicy;
import static org.umetadata.service.migration.utils.v160.MigrationUtil.addRelationsForTableConstraints;
import static org.umetadata.service.migration.utils.v160.MigrationUtil.addViewAllRuleToOrgPolicy;
import static org.umetadata.service.migration.utils.v160.MigrationUtil.migrateServiceTypesAndConnections;

import lombok.SneakyThrows;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.utils.MigrationFile;

public class Migration extends MigrationProcessImpl {

  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    migrateServiceTypesAndConnections(handle, false);
    addViewAllRuleToOrgPolicy(collectionDAO);
    addEditGlossaryTermsToDataConsumerPolicy(collectionDAO);
    addDisplayNameToCustomProperty(handle, false);
    addRelationsForTableConstraints(handle, false);
  }
}
