package org.umetadata.sdk.entities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.umetadata.schema.api.data.CreateTable;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.services.dataassets.TableService;

/**
 * SDK wrapper for Table operations.
 * This class provides static methods for Table CRUD operations.
 * It does NOT extend the schema Table class to avoid naming conflicts.
 */
public class Table {
  private static UMetadataClient defaultClient;

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  private static UMetadataClient getClient() {
    if (defaultClient == null) {
      throw new IllegalStateException("Default client not set. Call setDefaultClient() first.");
    }
    return defaultClient;
  }

  // Static CRUD methods - Stripe style
  public static org.umetadata.schema.entity.data.Table create(CreateTable request) {
    // Pass CreateTable request directly to the API
    return getClient().tables().create(request);
  }

  public static org.umetadata.schema.entity.data.Table retrieve(String id) {
    return (org.umetadata.schema.entity.data.Table) getClient().tables().get(id);
  }

  public static org.umetadata.schema.entity.data.Table retrieve(String id, String fields) {
    return (org.umetadata.schema.entity.data.Table) getClient().tables().get(id, fields);
  }

  public static org.umetadata.schema.entity.data.Table retrieveByName(
      String fullyQualifiedName) {
    return (org.umetadata.schema.entity.data.Table)
        getClient().tables().getByName(fullyQualifiedName);
  }

  public static org.umetadata.schema.entity.data.Table retrieveByName(
      String fullyQualifiedName, String fields) {
    return (org.umetadata.schema.entity.data.Table)
        getClient().tables().getByName(fullyQualifiedName, fields);
  }

  public static TableCollection list() {
    return new TableCollection(getClient().tables());
  }

  public static TableCollection list(TableListParams params) {
    return new TableCollection(getClient().tables(), params);
  }

  public static org.umetadata.schema.entity.data.Table update(
      String id, org.umetadata.schema.entity.data.Table table) {
    return (org.umetadata.schema.entity.data.Table) getClient().tables().update(id, table);
  }

  public static org.umetadata.schema.entity.data.Table update(
      org.umetadata.schema.entity.data.Table table) {
    if (table.getId() == null) {
      throw new IllegalArgumentException("Table must have an ID for update");
    }
    return update(table.getId().toString(), table);
  }

  public static org.umetadata.schema.entity.data.Table patch(String id, String jsonPatch) {
    try {
      com.fasterxml.jackson.databind.ObjectMapper mapper =
          new com.fasterxml.jackson.databind.ObjectMapper();
      com.fasterxml.jackson.databind.JsonNode patchNode = mapper.readTree(jsonPatch);
      return getClient().tables().patch(id, patchNode);
    } catch (Exception e) {
      throw new org.umetadata.sdk.exceptions.UMetadataException(
          "Failed to parse JSON patch: " + e.getMessage(), e);
    }
  }

  public static void delete(String id) {
    delete(id, false, false);
  }

  public static void delete(String id, boolean recursive, boolean hardDelete) {
    TableService service = getClient().tables();
    Map<String, String> params = new HashMap<>();
    params.put("recursive", String.valueOf(recursive));
    params.put("hardDelete", String.valueOf(hardDelete));
    service.delete(id, params);
  }

  // Async operations
  public static CompletableFuture<org.umetadata.schema.entity.data.Table> createAsync(
      CreateTable request) {
    return CompletableFuture.supplyAsync(() -> create(request));
  }

  public static CompletableFuture<org.umetadata.schema.entity.data.Table> retrieveAsync(
      String id) {
    return CompletableFuture.supplyAsync(() -> retrieve(id));
  }

  public static CompletableFuture<Void> deleteAsync(
      String id, boolean recursive, boolean hardDelete) {
    return CompletableFuture.runAsync(() -> delete(id, recursive, hardDelete));
  }

  // CSV operations
  public static String exportCsv(String name) {
    return getClient().tables().exportCsv(name);
  }

  public static CompletableFuture<String> exportCsvAsync(String name) {
    return CompletableFuture.supplyAsync(() -> exportCsv(name));
  }

  public static String importCsv(String name, String csvData, boolean dryRun) {
    return getClient().tables().importCsv(name, csvData, dryRun);
  }

  public static CompletableFuture<String> importCsvAsync(String name, String csvData) {
    return CompletableFuture.supplyAsync(() -> getClient().tables().importCsvAsync(name, csvData));
  }

  // Collection class with iterator support
  public static class TableCollection {
    private final TableService service;
    private final TableListParams params;

    TableCollection(TableService service) {
      this(service, null);
    }

    TableCollection(TableService service, TableListParams params) {
      this.service = service;
      this.params = params;
    }

    public java.util.List<org.umetadata.schema.entity.data.Table> getData() {
      ListParams listParams = params != null ? params.toListParams() : new ListParams();
      var response = service.list(listParams);
      return response.getData();
    }

    public Iterable<org.umetadata.schema.entity.data.Table> autoPagingIterable() {
      return new Iterable<org.umetadata.schema.entity.data.Table>() {
        @Override
        public Iterator<org.umetadata.schema.entity.data.Table> iterator() {
          return new TableIterator(service, params);
        }
      };
    }
  }

  // List params
  public static class TableListParams {
    private Integer limit;
    private String before;
    private String after;
    private String fields;
    private String domain;
    private String database;
    private String databaseSchema;

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private TableListParams params = new TableListParams();

      public Builder limit(Integer limit) {
        params.limit = limit;
        return this;
      }

      public Builder before(String before) {
        params.before = before;
        return this;
      }

      public Builder after(String after) {
        params.after = after;
        return this;
      }

      public Builder fields(String fields) {
        params.fields = fields;
        return this;
      }

      public Builder domain(String domain) {
        params.domain = domain;
        return this;
      }

      public Builder database(String database) {
        params.database = database;
        return this;
      }

      public Builder databaseSchema(String schema) {
        params.databaseSchema = schema;
        return this;
      }

      public TableListParams build() {
        return params;
      }
    }

    public ListParams toListParams() {
      ListParams listParams = new ListParams();
      if (limit != null) listParams.setLimit(limit);
      if (before != null) listParams.setBefore(before);
      if (after != null) listParams.setAfter(after);
      if (fields != null) listParams.setFields(fields);
      // Additional params can be added to ListParams extensions
      return listParams;
    }
  }

  // Iterator for pagination
  private static class TableIterator
      implements Iterator<org.umetadata.schema.entity.data.Table> {
    private final TableService service;
    private final TableListParams params;
    private Iterator<org.umetadata.schema.entity.data.Table> currentPage;
    private String nextPageToken;
    private boolean hasMore = true;

    TableIterator(TableService service, TableListParams params) {
      this.service = service;
      this.params = params;
      loadNextPage();
    }

    private void loadNextPage() {
      if (!hasMore) {
        return;
      }

      ListParams listParams = params != null ? params.toListParams() : new ListParams();
      if (nextPageToken != null) {
        listParams.setAfter(nextPageToken);
      }

      var response = service.list(listParams);
      currentPage = response.getData().iterator();
      nextPageToken = response.getPaging() != null ? response.getPaging().getAfter() : null;
      hasMore = nextPageToken != null;
    }

    @Override
    public boolean hasNext() {
      if (currentPage.hasNext()) {
        return true;
      }
      if (hasMore) {
        loadNextPage();
        return currentPage.hasNext();
      }
      return false;
    }

    @Override
    public org.umetadata.schema.entity.data.Table next() {
      if (!hasNext()) {
        throw new java.util.NoSuchElementException();
      }
      return currentPage.next();
    }
  }
}
