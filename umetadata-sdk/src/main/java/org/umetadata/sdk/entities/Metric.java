package org.umetadata.sdk.entities;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class Metric extends org.umetadata.schema.entity.data.Metric {

  // Static methods for CRUD operations
  public static Metric create(org.umetadata.schema.entity.data.Metric metric)
      throws UMetadataException {
    return (Metric) UMetadata.client().metrics().create(metric);
  }

  public static Metric retrieve(String id) throws UMetadataException {
    return (Metric) UMetadata.client().metrics().get(id);
  }

  public static Metric retrieve(String id, String fields) throws UMetadataException {
    return (Metric) UMetadata.client().metrics().get(id, fields);
  }

  public static Metric retrieve(UUID id) throws UMetadataException {
    return (Metric) UMetadata.client().metrics().get(id);
  }

  public static Metric retrieveByName(String name) throws UMetadataException {
    return (Metric) UMetadata.client().metrics().getByName(name);
  }

  public static Metric retrieveByName(String name, String fields) throws UMetadataException {
    return (Metric) UMetadata.client().metrics().getByName(name, fields);
  }

  public static MetricCollection list() throws UMetadataException {
    return new MetricCollection(UMetadata.client().metrics().list());
  }

  public static MetricCollection list(ListParams params) throws UMetadataException {
    return new MetricCollection(UMetadata.client().metrics().list(params));
  }

  public static void delete(String id) throws UMetadataException {
    UMetadata.client().metrics().delete(id);
  }

  public static void delete(UUID id) throws UMetadataException {
    UMetadata.client().metrics().delete(id);
  }

  // Async delete methods
  public static CompletableFuture<Void> deleteAsync(String id) {
    return UMetadata.client().metrics().deleteAsync(id);
  }

  public static CompletableFuture<Void> deleteAsync(UUID id) {
    return UMetadata.client().metrics().deleteAsync(id);
  }

  // Export/Import methods
  public static String exportCsv(String name) throws UMetadataException {
    return UMetadata.client().metrics().exportCsv(name);
  }

  public static String importCsv(String name, String csvData) throws UMetadataException {
    return UMetadata.client().metrics().importCsv(name, csvData);
  }

  public static String importCsv(String name, String csvData, boolean dryRun)
      throws UMetadataException {
    return UMetadata.client().metrics().importCsv(name, csvData, dryRun);
  }

  // Instance methods
  public Metric save() throws UMetadataException {
    if (this.getId() == null) {
      return (Metric) UMetadata.client().metrics().create(this);
    } else {
      return (Metric) UMetadata.client().metrics().update(this.getId(), this);
    }
  }

  public Metric update() throws UMetadataException {
    if (this.getId() == null) {
      throw new IllegalStateException("Cannot update a metric without an ID");
    }
    return (Metric) UMetadata.client().metrics().update(this.getId(), this);
  }

  public void delete() throws UMetadataException {
    if (this.getId() == null) {
      throw new IllegalStateException("Cannot delete a metric without an ID");
    }
    UMetadata.client().metrics().delete(this.getId());
  }

  // Fluent API methods
  public Metric addTags(List<org.umetadata.schema.type.TagLabel> tags) {
    if (this.getTags() == null) {
      this.setTags(tags);
    } else {
      this.getTags().addAll(tags);
    }
    return this;
  }

  public Metric setOwner(org.umetadata.schema.type.EntityReference owner) {
    this.setOwner(owner);
    return this;
  }

  // Static builder methods for list/retrieve params
  public static class ListBuilder {
    private final ListParams params = new ListParams();

    public ListBuilder fields(String fields) {
      params.setFields(fields);
      return this;
    }

    public ListBuilder limit(int limit) {
      params.setLimit(limit);
      return this;
    }

    public ListBuilder before(String before) {
      params.setBefore(before);
      return this;
    }

    public ListBuilder after(String after) {
      params.setAfter(after);
      return this;
    }

    public ListBuilder include(String include) {
      params.setFields(include);
      return this;
    }

    public MetricCollection list() throws UMetadataException {
      return Metric.list(params);
    }
  }

  public static ListBuilder listBuilder() {
    return new ListBuilder();
  }

  // Collection class with auto-pagination
  public static class MetricCollection
      implements Iterable<org.umetadata.schema.entity.data.Metric> {
    private final ListResponse<org.umetadata.schema.entity.data.Metric> response;

    public MetricCollection(ListResponse<org.umetadata.schema.entity.data.Metric> response) {
      this.response = response;
    }

    public List<org.umetadata.schema.entity.data.Metric> getData() {
      return response.getData();
    }

    public boolean hasNextPage() {
      return response.hasNextPage();
    }

    public boolean hasPreviousPage() {
      return response.hasPreviousPage();
    }

    public int getTotal() {
      return response.getTotal();
    }

    public MetricCollection nextPage() throws UMetadataException {
      if (!hasNextPage()) {
        throw new IllegalStateException("No next page available");
      }
      ListParams params = new ListParams().setAfter(response.getPaging().getAfter());
      return Metric.list(params);
    }

    public MetricCollection previousPage() throws UMetadataException {
      if (!hasPreviousPage()) {
        throw new IllegalStateException("No previous page available");
      }
      ListParams params = new ListParams().setBefore(response.getPaging().getBefore());
      return Metric.list(params);
    }

    @Override
    public Iterator<org.umetadata.schema.entity.data.Metric> iterator() {
      return response.getData().iterator();
    }
  }
}
