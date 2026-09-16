package org.umetadata.service.apps.bundles.searchIndex;

import java.util.Optional;
import org.umetadata.schema.system.Stats;

/**
 * Strategy interface for reindexing execution. Encapsulates the differences between single-server
 * and distributed indexing so that SearchIndexApp uses a single code path regardless of mode.
 */
public interface IndexingStrategy {

  void addListener(ReindexingProgressListener listener);

  ExecutionResult execute(ReindexingConfiguration config, ReindexingJobContext context);

  Optional<Stats> getStats();

  void stop();

  boolean isStopped();
}
