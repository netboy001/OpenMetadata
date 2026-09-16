package org.umetadata.service.migration.postgres.v184;

import static org.umetadata.service.migration.utils.v184.MigrationUtil.updateSearchSettings;

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
    updateSearchSettings();
  }
}
