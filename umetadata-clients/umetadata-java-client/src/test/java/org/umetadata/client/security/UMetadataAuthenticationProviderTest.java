package org.umetadata.client.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.umetadata.schema.security.client.UMetadataJWTClientConfig;
import org.umetadata.schema.services.connections.metadata.AuthProvider;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;

public class UMetadataAuthenticationProviderTest {

  private UMetadataAuthenticationProvider provider;

  @BeforeEach
  public void setUp() {
    UMetadataConnection connection =
        new UMetadataConnection()
            .withAuthProvider(AuthProvider.UMETADATA)
            .withHostPort("http://localhost:8585/api")
            .withSecurityConfig(new UMetadataJWTClientConfig().withJwtToken("test-token"));

    provider = new UMetadataAuthenticationProvider(connection);
  }

  @Test
  public void testAuthenticationAddedForEntityNamesContainingVersion() {
    RequestTemplate template = new RequestTemplate();
    template.uri("/v1/teams/name/data-conversion-service");
    provider.apply(template);

    assertTrue(
        template.headers().containsKey("Authorization"),
        "Authorization header should be added for entities with 'conversion' in name");
  }

  @Test
  public void testAuthenticationAddedForEntityNamesWithVersionSubstring() {
    RequestTemplate template = new RequestTemplate();
    template.uri("/v1/topics/name/dataset-version-update-events");
    provider.apply(template);

    assertTrue(
        template.headers().containsKey("Authorization"),
        "Authorization header should be added for entities with 'version' in name");
  }

  @Test
  public void testAuthenticationSkippedForVersionEndpoint() {
    RequestTemplate template = new RequestTemplate();
    template.uri("/v1/system/version");
    provider.apply(template);

    assertFalse(
        template.headers().containsKey("Authorization"),
        "Authorization header should NOT be added for /system/version endpoint");
  }

  @Test
  public void testAuthenticationSkippedForVersionEndpointWithQueryParams() {
    RequestTemplate template = new RequestTemplate();
    template.uri("/v1/system/version?param=value");
    provider.apply(template);

    assertFalse(
        template.headers().containsKey("Authorization"),
        "Authorization header should NOT be added for /system/version endpoint with query params");
  }

  @Test
  public void testAuthenticationAddedForRegularEndpoints() {
    RequestTemplate template = new RequestTemplate();
    template.uri("/v1/teams/name/my-team");
    provider.apply(template);

    assertTrue(
        template.headers().containsKey("Authorization"),
        "Authorization header should be added for regular endpoints");
  }
}
