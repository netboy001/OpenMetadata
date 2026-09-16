package org.umetadata.service.migration.mysql.v172;

import static org.umetadata.service.migration.utils.v172.MigrationUtil.removeOldDataInsightsObjects;

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
    // Data Insights
    removeOldDataInsightsObjects();
  }
}
