package org.umetadata.service.apps.bundles.test;

import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.entity.app.App;
import org.umetadata.service.apps.AbstractNativeApplication;
import org.umetadata.service.jdbi3.CollectionDAO;
import org.umetadata.service.search.SearchRepository;

@Slf4j
@SuppressWarnings("unused")
public class NoOpTestApplication extends AbstractNativeApplication {

  public NoOpTestApplication(CollectionDAO collectionDAO, SearchRepository searchRepository) {
    super(collectionDAO, searchRepository);
  }

  @Override
  public void init(App app) {
    super.init(app);
    LOG.info("NoOpTestApplication is initialized");
  }
}
