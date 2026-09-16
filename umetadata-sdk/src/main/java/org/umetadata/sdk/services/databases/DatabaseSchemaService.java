package org.umetadata.sdk.services.databases;

import java.util.UUID;
import org.umetadata.schema.api.data.CreateDatabaseSchema;
import org.umetadata.schema.entity.data.DatabaseSchema;
import org.umetadata.schema.type.DatabaseSchemaProfilerConfig;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class DatabaseSchemaService extends EntityServiceBase<DatabaseSchema> {
  public DatabaseSchemaService(HttpClient httpClient) {
    super(httpClient, "/v1/databaseSchemas");
  }

  @Override
  protected Class<DatabaseSchema> getEntityClass() {
    return DatabaseSchema.class;
  }

  // Create database schema using CreateDatabaseSchema request
  public DatabaseSchema create(CreateDatabaseSchema request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, DatabaseSchema.class);
  }

  /**
   * Add or update profiler config for a database schema.
   *
   * @param id Schema ID
   * @param profilerConfig Profiler config to add
   * @return Updated database schema
   * @throws UMetadataException if request fails
   */
  public DatabaseSchema addProfilerConfig(UUID id, DatabaseSchemaProfilerConfig profilerConfig)
      throws UMetadataException {
    return addProfilerConfig(id.toString(), profilerConfig);
  }

  /**
   * Add or update profiler config for a database schema.
   *
   * @param id Schema ID as string
   * @param profilerConfig Profiler config to add
   * @return Updated database schema
   * @throws UMetadataException if request fails
   */
  public DatabaseSchema addProfilerConfig(String id, DatabaseSchemaProfilerConfig profilerConfig)
      throws UMetadataException {
    String path = basePath + "/" + id + "/databaseSchemaProfilerConfig";
    return httpClient.execute(HttpMethod.PUT, path, profilerConfig, DatabaseSchema.class);
  }

  /**
   * Get profiler config for a database schema.
   *
   * @param id Schema ID
   * @return Database schema with profiler config
   * @throws UMetadataException if request fails
   */
  public DatabaseSchema getProfilerConfig(UUID id) throws UMetadataException {
    return getProfilerConfig(id.toString());
  }

  /**
   * Get profiler config for a database schema.
   *
   * @param id Schema ID as string
   * @return Database schema with profiler config
   * @throws UMetadataException if request fails
   */
  public DatabaseSchema getProfilerConfig(String id) throws UMetadataException {
    String path = basePath + "/" + id + "/databaseSchemaProfilerConfig";
    return httpClient.execute(HttpMethod.GET, path, null, DatabaseSchema.class);
  }
}
