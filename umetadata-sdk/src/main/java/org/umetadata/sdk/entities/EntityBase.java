package org.umetadata.sdk.entities;

import org.umetadata.sdk.client.UMetadataClient;

public abstract class EntityBase {
  protected static UMetadataClient defaultClient;

  public static void setDefaultClient(UMetadataClient client) {
    defaultClient = client;
  }

  protected static UMetadataClient getClient() {
    if (defaultClient == null) {
      throw new IllegalStateException(
          "Default client not set. Call setDefaultClient() or use instance methods.");
    }
    return defaultClient;
  }
}
