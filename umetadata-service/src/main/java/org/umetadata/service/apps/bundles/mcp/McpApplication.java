package org.umetadata.service.apps.bundles.mcp;

import lombok.extern.slf4j.Slf4j;
import org.umetadata.service.apps.AbstractNativeApplication;
import org.umetadata.service.jdbi3.CollectionDAO;
import org.umetadata.service.search.SearchRepository;

@Slf4j
public class McpApplication extends AbstractNativeApplication {
  public McpApplication(CollectionDAO collectionDAO, SearchRepository searchRepository) {
    super(collectionDAO, searchRepository);
  }
}
