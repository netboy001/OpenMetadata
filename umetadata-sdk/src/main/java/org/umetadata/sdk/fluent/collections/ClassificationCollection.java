package org.umetadata.sdk.fluent.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.umetadata.schema.entity.classification.Classification;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

/**
 * Collection for iterating over Classification entities with auto-pagination support.
 */
public class ClassificationCollection implements Iterable<Classification> {
  private final UMetadataClient client;
  private final ListParams params;
  private List<Classification> currentPage;
  private String nextPageToken;
  private boolean hasMore;

  public ClassificationCollection(UMetadataClient client) {
    this(client, new ListParams());
  }

  public ClassificationCollection(UMetadataClient client, ListParams params) {
    this.client = client;
    this.params = params;
    this.hasMore = true;
  }

  public ClassificationCollection limit(int limit) {
    params.setLimit(limit);
    return this;
  }

  public List<Classification> getCurrentPage() {
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

      ListResponse<Classification> response = client.classifications().list(pageParams);
      currentPage = response.getData();
      nextPageToken = response.getPaging() != null ? response.getPaging().getAfter() : null;
      hasMore = nextPageToken != null && !currentPage.isEmpty();
      return true;
    } catch (Exception e) {
      throw new RuntimeException("Failed to load next page", e);
    }
  }

  @Override
  public Iterator<Classification> iterator() {
    return getCurrentPage().iterator();
  }

  public Stream<Classification> stream() {
    return getCurrentPage().stream();
  }

  public List<Classification> toList() {
    List<Classification> all = new ArrayList<>();
    do {
      all.addAll(getCurrentPage());
    } while (loadNextPage());
    return all;
  }
}
