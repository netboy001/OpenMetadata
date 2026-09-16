package org.umetadata.service.migration.postgres.v1104;

import lombok.SneakyThrows;
import org.umetadata.service.jdbi3.locator.ConnectionType;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.utils.MigrationFile;
import org.umetadata.service.migration.utils.v1104.MigrationUtil;

public class Migration extends MigrationProcessImpl {

  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    MigrationUtil migrationUtil = new MigrationUtil(handle, ConnectionType.POSTGRES);
    migrationUtil.migrateEntityExtensionStatus();
  }
}
