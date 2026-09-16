package org.umetadata.sdk.fluent;

import java.util.*;
import org.umetadata.schema.api.data.CreateSpreadsheet;
import org.umetadata.schema.entity.data.Spreadsheet;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.sdk.client.UMetadataClient;

public final class Spreadsheets {
  private static UMetadataClient defaultClient;

  private Spreadsheets() {}

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  private static UMetadataClient getClient() {
    if (defaultClient == null) {
      throw new IllegalStateException(
          "Client not initialized. Call Spreadsheets.setDefaultClient() first.");
    }
    return defaultClient;
  }

  public static SpreadsheetCreator create() {
    return new SpreadsheetCreator(getClient());
  }

  public static Spreadsheet get(String id) {
    return getClient().spreadsheets().get(id);
  }

  public static Spreadsheet getByName(String fqn) {
    return getClient().spreadsheets().getByName(fqn);
  }

  public static Spreadsheet getByName(String fqn, String fields) {
    return getClient().spreadsheets().getByName(fqn, fields);
  }

  public static void delete(String id) {
    getClient().spreadsheets().delete(id);
  }

  public static SpreadsheetFinder find(String id) {
    return new SpreadsheetFinder(getClient(), id, false);
  }

  public static SpreadsheetFinder findByName(String fqn) {
    return new SpreadsheetFinder(getClient(), fqn, true);
  }

  public static SpreadsheetUpdater update(String id) {
    return new SpreadsheetUpdater(getClient(), id);
  }

  public static class SpreadsheetCreator {
    private final UMetadataClient client;
    private final CreateSpreadsheet request = new CreateSpreadsheet();

    SpreadsheetCreator(UMetadataClient client) {
      this.client = client;
    }

    public SpreadsheetCreator name(String name) {
      request.setName(name);
      return this;
    }

    public SpreadsheetCreator withDisplayName(String displayName) {
      request.setDisplayName(displayName);
      return this;
    }

    public SpreadsheetCreator withDescription(String description) {
      request.setDescription(description);
      return this;
    }

    public SpreadsheetCreator withService(String serviceFqn) {
      request.setService(serviceFqn);
      return this;
    }

    public SpreadsheetCreator withOwners(List<EntityReference> owners) {
      request.setOwners(owners);
      return this;
    }

    public SpreadsheetCreator withParent(EntityReference parent) {
      request.setParent(parent);
      return this;
    }

    public Spreadsheet execute() {
      return client.spreadsheets().create(request);
    }
  }

  public static class SpreadsheetFinder {
    private final UMetadataClient client;
    private final String identifier;
    private final boolean isFqn;
    private final Set<String> includes = new HashSet<>();

    SpreadsheetFinder(UMetadataClient client, String identifier, boolean isFqn) {
      this.client = client;
      this.identifier = identifier;
      this.isFqn = isFqn;
    }

    public SpreadsheetFinder withFields(String... fields) {
      includes.addAll(Arrays.asList(fields));
      return this;
    }

    public Spreadsheet fetch() {
      if (includes.isEmpty()) {
        return isFqn
            ? client.spreadsheets().getByName(identifier)
            : client.spreadsheets().get(identifier);
      }
      String fields = String.join(",", includes);
      return isFqn
          ? client.spreadsheets().getByName(identifier, fields)
          : client.spreadsheets().get(identifier, fields);
    }
  }

  public static class SpreadsheetUpdater {
    private final UMetadataClient client;
    private final String id;
    private Spreadsheet entityToUpdate;

    SpreadsheetUpdater(UMetadataClient client, String id) {
      this.client = client;
      this.id = id;
    }

    public SpreadsheetUpdater entity(Spreadsheet entity) {
      this.entityToUpdate = entity;
      return this;
    }

    public Spreadsheet execute() {
      if (entityToUpdate == null) {
        throw new IllegalStateException("Entity to update must be set via entity() method");
      }
      return client.spreadsheets().update(id, entityToUpdate);
    }
  }
}
