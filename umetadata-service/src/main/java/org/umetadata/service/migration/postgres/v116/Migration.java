package org.umetadata.service.migration.postgres.v116;

import static org.umetadata.service.migration.utils.V112.MigrationUtil.lowerCaseUserNameAndEmail;
import static org.umetadata.service.migration.utils.V114.MigrationUtil.fixTestSuites;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.utils.MigrationFile;

@Slf4j
public class Migration extends MigrationProcessImpl {
  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    fixTestSuites(collectionDAO);
    lowerCaseUserNameAndEmail(collectionDAO);
  }
}
