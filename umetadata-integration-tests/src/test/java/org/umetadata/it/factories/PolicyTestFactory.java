package org.umetadata.it.factories;

import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.schema.entity.policies.Policy;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.exceptions.UMetadataException;

/**
 * Factory for accessing predefined Policy entities in integration tests.
 *
 * <p>Uses the SDK client to access policies that are predefined in the UMetadata system.
 */
public class PolicyTestFactory {

  /**
   * Get the DataConsumerPolicy (predefined in the system).
   */
  public static Policy getDataConsumerPolicy(TestNamespace ns) {
    return getPolicyByName("DataConsumerPolicy");
  }

  /**
   * Get the DataStewardPolicy (predefined in the system).
   */
  public static Policy getDataStewardPolicy(TestNamespace ns) {
    return getPolicyByName("DataStewardPolicy");
  }

  /**
   * Get the OrganizationPolicy (predefined in the system).
   */
  public static Policy getOrganizationPolicy(TestNamespace ns) {
    return getPolicyByName("OrganizationPolicy");
  }

  /**
   * Get a policy by name.
   */
  public static Policy getPolicyByName(String policyName) {
    try {
      UMetadataClient client = SdkClients.adminClient();
      return client.policies().getByName(policyName);
    } catch (UMetadataException e) {
      throw new RuntimeException("Policy not found: " + policyName, e);
    }
  }
}
