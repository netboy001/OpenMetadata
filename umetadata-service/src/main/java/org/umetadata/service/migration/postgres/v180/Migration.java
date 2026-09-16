package org.umetadata.service.migration.postgres.v180;

import static org.umetadata.service.migration.utils.v180.MigrationUtil.addCertificationOperationsToPolicy;
import static org.umetadata.service.migration.utils.v180.MigrationUtil.addDenyDisplayNameRuleToBotPolicies;

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
    addCertificationOperationsToPolicy(collectionDAO);
    addDenyDisplayNameRuleToBotPolicies(collectionDAO);
  }
}
