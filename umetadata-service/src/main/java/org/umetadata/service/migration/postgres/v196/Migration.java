package org.umetadata.service.migration.postgres.v196;

import lombok.SneakyThrows;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.utils.MigrationFile;
import org.umetadata.service.migration.utils.v196.MigrationUtil;

public class Migration extends MigrationProcessImpl {

  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    // Automator
    MigrationUtil migrationUtil = new MigrationUtil(collectionDAO);
    migrationUtil.migrateAutomatorTagsAndTerms(handle);
  }
}
