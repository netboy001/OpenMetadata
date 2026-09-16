package org.umetadata.service.migration.mysql.v120;

import static org.umetadata.service.migration.utils.v120.MigrationUtil.addQueryService;
import static org.umetadata.service.migration.utils.v120.MigrationUtil.updateGlossaryAndGlossaryTermRelations;

import java.util.List;
import lombok.SneakyThrows;
import org.umetadata.service.migration.api.MigrationProcessImpl;
import org.umetadata.service.migration.context.MigrationOps;
import org.umetadata.service.migration.utils.MigrationFile;

public class Migration extends MigrationProcessImpl {
  public Migration(MigrationFile migrationFile) {
    super(migrationFile);
  }

  @Override
  @SneakyThrows
  public void runDataMigration() {
    addQueryService(handle, collectionDAO);
    updateGlossaryAndGlossaryTermRelations(handle, collectionDAO);
  }

  @Override
  public List<MigrationOps> getMigrationOps() {
    return List.of(new MigrationOps("queryCount", "SELECT COUNT(*) FROM query_entity"));
  }
}
