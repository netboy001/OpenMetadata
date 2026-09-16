package org.umetadata.sdk.fluent;

import org.umetadata.schema.api.teams.CreatePersona;
import org.umetadata.schema.entity.teams.Persona;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * Pure Fluent API for Persona operations.
 */
public final class Personas {
  private static UMetadataClient defaultClient;

  private Personas() {} // Prevent instantiation

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  private static UMetadataClient getClient() {
    if (defaultClient == null) {
      throw new IllegalStateException(
          "Client not initialized. Call Personas.setDefaultClient() first.");
    }
    return defaultClient;
  }

  // ==================== Creation ====================

  public static Persona create(CreatePersona request) {
    return getClient().personas().create(request);
  }

  // ==================== Direct Access Methods ====================

  public static Persona get(String id) {
    return getClient().personas().get(id);
  }

  public static Persona get(String id, String fields) {
    return getClient().personas().get(id, fields);
  }

  public static Persona getByName(String fqn) {
    return getClient().personas().getByName(fqn);
  }

  public static Persona getByName(String fqn, String fields) {
    return getClient().personas().getByName(fqn, fields);
  }

  public static Persona update(String id, Persona entity) {
    return getClient().personas().update(id, entity);
  }

  public static void delete(String id) {
    getClient().personas().delete(id);
  }

  public static void delete(String id, java.util.Map<String, String> params) {
    getClient().personas().delete(id, params);
  }

  public static void restore(String id) {
    getClient().personas().restore(id);
  }

  public static org.umetadata.sdk.models.ListResponse<Persona> list(
      org.umetadata.sdk.models.ListParams params) {
    return getClient().personas().list(params);
  }
}
