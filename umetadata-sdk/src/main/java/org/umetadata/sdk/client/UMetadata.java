package org.umetadata.sdk.client;

import org.umetadata.sdk.config.UMetadataConfig;

public class UMetadata {
  private static UMetadataClient defaultClient;

  private UMetadata() {
    // Private constructor to prevent instantiation
  }

  public static void initialize(UMetadataConfig config) {
    defaultClient = new UMetadataClient(config);
  }

  public static void initialize(String baseUrl, String accessToken) {
    UMetadataConfig config =
        UMetadataConfig.builder().baseUrl(baseUrl).accessToken(accessToken).build();
    initialize(config);
  }

  public static UMetadataClient client() {
    if (defaultClient == null) {
      throw new IllegalStateException(
          "UMetadata client not initialized. Call UMetadata.initialize() first.");
    }
    return defaultClient;
  }

  public static UMetadataClient client(UMetadataConfig config) {
    return new UMetadataClient(config);
  }

  public static boolean isInitialized() {
    return defaultClient != null;
  }

  public static void reset() {
    defaultClient = null;
  }
}
