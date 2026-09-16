package org.umetadata.sdk.fluent.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.umetadata.schema.tests.TestSuite;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

/**
 * Collection for iterating over TestSuite entities with auto-pagination support.
 */
public class TestSuiteCollection implements Iterable<TestSuite> {
  private final UMetadataClient client;
  private final ListParams params;
  private List<TestSuite> currentPage;
  private String nextPageToken;
  private boolean hasMore;

  public TestSuiteCollection(UMetadataClient client) {
    this(client, new ListParams());
  }

  public TestSuiteCollection(UMetadataClient client, ListParams params) {
    this.client = client;
    this.params = params;
    this.hasMore = true;
  }

  public TestSuiteCollection limit(int limit) {
    params.setLimit(limit);
    return this;
  }

  public List<TestSuite> getCurrentPage() {
    if (currentPage == null) {
      loadNextPage();
    }
    return currentPage;
  }

  public boolean loadNextPage() {
    if (!hasMore) {
      return false;
    }

    try {
      ListParams pageParams = new ListParams();
      pageParams.setLimit(params.getLimit());
      if (nextPageToken != null) {
        pageParams.setAfter(nextPageToken);
      }

      ListResponse<TestSuite> response = client.testSuites().list(pageParams);
      currentPage = response.getData();
      nextPageToken = response.getPaging() != null ? response.getPaging().getAfter() : null;
      hasMore = nextPageToken != null && !currentPage.isEmpty();
      return true;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load next page", e);
    }
  }

  @Override
  public Iterator<TestSuite> iterator() {
    return getCurrentPage().iterator();
  }

  public Stream<TestSuite> stream() {
    return getCurrentPage().stream();
  }

  public List<TestSuite> toList() {
    List<TestSuite> all = new ArrayList<>();
    do {
      all.addAll(getCurrentPage());
    } while (loadNextPage());
    return all;
  }
}
