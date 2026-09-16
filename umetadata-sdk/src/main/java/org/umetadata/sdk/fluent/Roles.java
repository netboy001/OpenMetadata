package org.umetadata.sdk.fluent;

import java.util.*;
import org.umetadata.schema.api.teams.CreateRole;
import org.umetadata.schema.entity.teams.Role;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * Pure Fluent API for Role operations.
 */
public final class Roles {
  private static UMetadataClient defaultClient;

  private Roles() {} // Prevent instantiation

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  private static UMetadataClient getClient() {
    if (defaultClient == null) {
      throw new IllegalStateException(
          "Client not initialized. Call Roles.setDefaultClient() first.");
    }
    return defaultClient;
  }

  // ==================== Creation ====================

  public static Role create(CreateRole request) {
    return getClient().roles().create(request);
  }

  // ==================== Direct Access Methods ====================

  public static Role get(String id) {
    return getClient().roles().get(id);
  }

  public static Role get(String id, String fields) {
    return getClient().roles().get(id, fields);
  }

  public static Role get(String id, String fields, String include) {
    return getClient().roles().get(id, fields, include);
  }

  public static Role getByName(String fqn) {
    return getClient().roles().getByName(fqn);
  }

  public static Role getByName(String fqn, String fields) {
    return getClient().roles().getByName(fqn, fields);
  }

  public static Role update(String id, Role entity) {
    return getClient().roles().update(id, entity);
  }

  public static void delete(String id) {
    getClient().roles().delete(id);
  }

  public static void delete(String id, java.util.Map<String, String> params) {
    getClient().roles().delete(id, params);
  }

  public static void restore(String id) {
    getClient().roles().restore(id);
  }

  public static org.umetadata.sdk.models.ListResponse<Role> list(
      org.umetadata.sdk.models.ListParams params) {
    return getClient().roles().list(params);
  }

  public static org.umetadata.schema.type.EntityHistory getVersionList(java.util.UUID id) {
    return getClient().roles().getVersionList(id);
  }

  public static Role getVersion(String id, Double version) {
    return getClient().roles().getVersion(id, version);
  }
}
