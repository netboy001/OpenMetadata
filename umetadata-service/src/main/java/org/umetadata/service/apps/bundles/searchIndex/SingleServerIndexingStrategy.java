package org.umetadata.service.apps.bundles.searchIndex;

import java.util.Optional;
import org.umetadata.schema.system.Stats;
import org.umetadata.service.jdbi3.CollectionDAO;
import org.umetadata.service.search.SearchRepository;

public class SingleServerIndexingStrategy implements IndexingStrategy {

  private final SearchIndexExecutor executor;

  public SingleServerIndexingStrategy(
      CollectionDAO collectionDAO, SearchRepository searchRepository) {
    this.executor = new SearchIndexExecutor(collectionDAO, searchRepository);
  }

  @Override
  public void addListener(ReindexingProgressListener listener) {
    executor.addListener(listener);
  }

  @Override
  public ExecutionResult execute(ReindexingConfiguration config, ReindexingJobContext context) {
    return executor.execute(config, context);
  }

  @Override
  public Optional<Stats> getStats() {
    return Optional.ofNullable(executor.getStats().get());
  }

  @Override
  public void stop() {
    executor.stop();
  }

  @Override
  public boolean isStopped() {
    return executor.isStopped();
  }
}
