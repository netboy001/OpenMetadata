package org.umetadata.service.migration.postgres.v171;

import static org.umetadata.service.migration.utils.v171.MigrationUtil.updateServiceCharts;
import static org.umetadata.service.migration.utils.v171.MigrationUtil.updateWorkflowDefinitions;

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
    updateServiceCharts();

    // Updating WorkflowDefinition - wrap in try-catch to prevent blocking other migrations
    try {
      initializeWorkflowHandler();
      updateWorkflowDefinitions();
    } catch (Exception e) {
      LOG.error(
          "Failed to initialize WorkflowHandler or update workflows in v171 migration. "
              + "Workflow features may not work correctly until server restart.",
          e);
    }
  }
}
