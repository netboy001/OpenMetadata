package org.umetadata.service.migration.postgres.v11212;

import lombok.SneakyThrows;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.utils.MigrationFile;
import org.umetadata.service.migration.utils.v11212.MigrationUtil;

public class Migration extends MigrationProcessImpl {

  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    initializeWorkflowHandler();
    MigrationUtil.repairStaleGlossaryApprovalFqns(handle, true);
  }
}
