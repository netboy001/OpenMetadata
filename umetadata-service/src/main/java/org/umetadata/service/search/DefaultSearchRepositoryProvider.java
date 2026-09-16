package org.umetadata.service.search;

import org.umetadata.schema.service.configuration.elasticsearch.ElasticSearchConfiguration;

/**
 * Implementation of SearchRepositoryProvider.
 * This is the default provider that creates standard UMetadata SearchRepository instances.
 */
public class DefaultSearchRepositoryProvider implements SearchRepositoryProvider {

  @Override
  public SearchRepository createSearchRepository(
      ElasticSearchConfiguration elasticSearchConfiguration, int maxSize) {
    return new SearchRepository(elasticSearchConfiguration, maxSize);
  }

  @Override
  public int getPriority() {
    return 0;
  }

  @Override
  public boolean isAvailable() {
    return true;
  }
}
