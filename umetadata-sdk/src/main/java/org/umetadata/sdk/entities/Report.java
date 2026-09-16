package org.umetadata.sdk.entities;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.client.UMetadata;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;

public class Report extends org.umetadata.schema.entity.data.Report {

  // Static methods for CRUD operations
  public static Report create(org.umetadata.schema.entity.data.Report report)
      throws UMetadataException {
    return (Report) UMetadata.client().reports().create(report);
  }

  public static Report retrieve(String id) throws UMetadataException {
    return (Report) UMetadata.client().reports().get(id);
  }

  public static Report retrieve(String id, String fields) throws UMetadataException {
    return (Report) UMetadata.client().reports().get(id, fields);
  }

  public static Report retrieve(UUID id) throws UMetadataException {
    return (Report) UMetadata.client().reports().get(id);
  }

  public static Report retrieveByName(String name) throws UMetadataException {
    return (Report) UMetadata.client().reports().getByName(name);
  }

  public static Report retrieveByName(String name, String fields) throws UMetadataException {
    return (Report) UMetadata.client().reports().getByName(name, fields);
  }

  public static ReportCollection list() throws UMetadataException {
    return new ReportCollection(UMetadata.client().reports().list());
  }

  public static ReportCollection list(ListParams params) throws UMetadataException {
    return new ReportCollection(UMetadata.client().reports().list(params));
  }

  public static void delete(String id) throws UMetadataException {
    UMetadata.client().reports().delete(id);
  }

  public static void delete(UUID id) throws UMetadataException {
    UMetadata.client().reports().delete(id);
  }

  // Async delete methods
  public static CompletableFuture<Void> deleteAsync(String id) {
    return UMetadata.client().reports().deleteAsync(id);
  }

  public static CompletableFuture<Void> deleteAsync(UUID id) {
    return UMetadata.client().reports().deleteAsync(id);
  }

  // Export/Import methods
  public static String exportCsv(String name) throws UMetadataException {
    return UMetadata.client().reports().exportCsv(name);
  }

  public static String importCsv(String name, String csvData) throws UMetadataException {
    return UMetadata.client().reports().importCsv(name, csvData);
  }

  public static String importCsv(String name, String csvData, boolean dryRun)
      throws UMetadataException {
    return UMetadata.client().reports().importCsv(name, csvData, dryRun);
  }

  // Instance methods
  public Report save() throws UMetadataException {
    if (this.getId() == null) {
      return (Report) UMetadata.client().reports().create(this);
    } else {
      return (Report) UMetadata.client().reports().update(this.getId(), this);
    }
  }

  public Report update() throws UMetadataException {
    if (this.getId() == null) {
      throw new IllegalStateException("Cannot update a report without an ID");
    }
    return (Report) UMetadata.client().reports().update(this.getId(), this);
  }

  public void delete() throws UMetadataException {
    if (this.getId() == null) {
      throw new IllegalStateException("Cannot delete a report without an ID");
    }
    UMetadata.client().reports().delete(this.getId());
  }

  // Fluent API methods
  public Report addTags(List<org.umetadata.schema.type.TagLabel> tags) {
    if (this.getTags() == null) {
      this.setTags(tags);
    } else {
      this.getTags().addAll(tags);
    }
    return this;
  }

  public Report setOwner(org.umetadata.schema.type.EntityReference owner) {
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

    public ReportCollection list() throws UMetadataException {
      return Report.list(params);
    }
  }

  public static ListBuilder listBuilder() {
    return new ListBuilder();
  }

  // Collection class with auto-pagination
  public static class ReportCollection
      implements Iterable<org.umetadata.schema.entity.data.Report> {
    private final ListResponse<org.umetadata.schema.entity.data.Report> response;

    public ReportCollection(ListResponse<org.umetadata.schema.entity.data.Report> response) {
      this.response = response;
    }

    public List<org.umetadata.schema.entity.data.Report> getData() {
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

    public ReportCollection nextPage() throws UMetadataException {
      if (!hasNextPage()) {
        throw new IllegalStateException("No next page available");
      }
      ListParams params = new ListParams().setAfter(response.getPaging().getAfter());
      return Report.list(params);
    }

    public ReportCollection previousPage() throws UMetadataException {
      if (!hasPreviousPage()) {
        throw new IllegalStateException("No previous page available");
      }
      ListParams params = new ListParams().setBefore(response.getPaging().getBefore());
      return Report.list(params);
    }

    @Override
    public Iterator<org.umetadata.schema.entity.data.Report> iterator() {
      return response.getData().iterator();
    }
  }
}
