package org.umetadata.service.migration.postgres.v133;

import static org.umetadata.service.migration.utils.v131.MigrationUtil.migrateCronExpression;

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
    migrateCronExpression(collectionDAO);
  }
}
