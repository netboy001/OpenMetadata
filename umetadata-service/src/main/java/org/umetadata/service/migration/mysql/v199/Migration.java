package org.umetadata.service.migration.mysql.v199;

import lombok.SneakyThrows;
import org.umetadata.service.jdbi3.locator.ConnectionType;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.utils.MigrationFile;
import org.umetadata.service.migration.utils.v199.MigrationUtil;

public class Migration extends MigrationProcessImpl {

  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    MigrationUtil migrationUtil = new MigrationUtil(handle, ConnectionType.MYSQL);
    migrationUtil.addUserActivityColumns();
  }
}
