package org.umetadata.sdk.services.storages;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Collections;
import java.util.List;
import org.umetadata.schema.api.data.CreateContainer;
import org.umetadata.schema.entity.data.Container;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.models.ListParams;
import org.umetadata.sdk.models.ListResponse;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.network.RequestOptions;
import org.umetadata.sdk.services.EntityServiceBase;

public class ContainerService extends EntityServiceBase<Container> {
  public ContainerService(HttpClient httpClient) {
    super(httpClient, "/v1/containers");
  }

  @Override
  protected Class<Container> getEntityClass() {
    return Container.class;
  }

  // Create container using CreateContainer request
  public Container create(CreateContainer request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, Container.class);
  }

  /**
   * Page through the immediate children of a Container via the dedicated
   * {@code /v1/containers/name/{fqn}/children} endpoint. Use this instead of fetching the
   * parent with {@code fields=children} — that field is no longer served because the inline
   * payload is unbounded for buckets with many objects.
   *
   * <p>Each row is a slim {@link Container} projection (id, name, displayName, fqn,
   * description, service); {@code dataModel}, {@code tags}, {@code owners}, {@code extension}
   * are not populated. Re-fetch the specific child via {@link #getByName(String)} when full
   * details are needed.
   */
  public ListResponse<Container> listChildren(String fqn, ListParams params)
      throws UMetadataException {
    String path = buildPathWithEncodedName(fqn) + "/children";
    RequestOptions options =
        RequestOptions.builder()
            .queryParams(params != null ? params.toQueryParams() : Collections.emptyMap())
            .build();
    String responseStr = httpClient.executeForString(HttpMethod.GET, path, null, options);
    return deserializeListResponse(responseStr);
  }

  public ListResponse<Container> listChildren(String fqn) throws UMetadataException {
    return listChildren(fqn, new ListParams());
  }

  /**
   * Resolve the full ancestor chain for a container in a single call. Returns
   * {@link EntityReference}s ordered from the root container (immediate child of the storage
   * service) down to the immediate parent of {@code fqn}. Empty when the container is at the
   * top level.
   */
  public List<EntityReference> listAncestors(String fqn) throws UMetadataException {
    String path = buildPathWithEncodedName(fqn) + "/ancestors";
    String responseStr = httpClient.executeForString(HttpMethod.GET, path, null, null);
    try {
      return objectMapper.readValue(responseStr, new TypeReference<List<EntityReference>>() {});
    } catch (Exception e) {
      throw new UMetadataException(
          "Failed to deserialize ancestors response: " + e.getMessage(), e);
    }
  }
}
