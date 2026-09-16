package org.umetadata.service.resources.apps;

import org.umetadata.service.apps.AbstractNativeApplication;
import org.umetadata.service.jdbi3.CollectionDAO;
import org.umetadata.service.search.SearchRepository;

public class TestApp extends AbstractNativeApplication {
  public TestApp(CollectionDAO collectionDAO, SearchRepository searchRepository) {
    super(collectionDAO, searchRepository);
  }
}
