package org.umetadata.it.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.umetadata.schema.configuration.EntityRulesSettings;
import org.umetadata.schema.settings.Settings;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.network.RequestOptions;

/**
 * Utility class for managing entity rules settings in integration tests. Provides methods to
 * toggle validation rules like multi-domain restrictions.
 */
public class EntityRulesUtil {

  private static final String MULTI_DOMAIN_RULE_NAME = "Multiple Domains are not allowed";
  private static final String SETTINGS_ENDPOINT = "/v1/system/settings";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Toggle the "Multiple Domains are not allowed" rule.
   *
   * @param client UMetadata admin client
   * @param enableRule true to ENABLE the rule (disallow multiple domains), false to DISABLE the
   *     rule (allow multiple domains)
   * @return the updated Settings object
   */
  public static Settings toggleMultiDomainRule(UMetadataClient client, boolean enableRule) {
    return toggleRule(client, MULTI_DOMAIN_RULE_NAME, enableRule);
  }

  public static boolean isMultiDomainRuleEnabled(UMetadataClient client) {
    return isRuleEnabled(client, MULTI_DOMAIN_RULE_NAME);
  }

  /**
   * Toggle any entity rule by name.
   *
   * @param client UMetadata admin client
   * @param ruleName name of the rule to toggle
   * @param enableRule true to enable the rule, false to disable
   * @return the updated Settings object
   */
  public static Settings toggleRule(
      UMetadataClient client, String ruleName, boolean enableRule) {
    try {
      // Get current entity rules settings
      Settings currentSettings = getEntityRulesSettings(client);

      // Extract EntityRulesSettings from configValue
      EntityRulesSettings entityRulesSettings =
          MAPPER.convertValue(currentSettings.getConfigValue(), EntityRulesSettings.class);

      // Find and toggle the specific rule
      boolean ruleFound = false;
      if (entityRulesSettings.getEntitySemantics() != null) {
        for (var rule : entityRulesSettings.getEntitySemantics()) {
          if (ruleName.equals(rule.getName())) {
            rule.setEnabled(enableRule);
            ruleFound = true;
            break;
          }
        }
      }

      if (!ruleFound) {
        throw new IllegalArgumentException("Rule not found: " + ruleName);
      }

      // Update the settings
      currentSettings.setConfigValue(entityRulesSettings);
      return updateSettings(client, currentSettings);

    } catch (Exception e) {
      throw new RuntimeException("Failed to toggle rule: " + ruleName, e);
    }
  }

  /**
   * Get current entity rules settings.
   *
   * @param client UMetadata client
   * @return Settings object containing entity rules
   */
  public static Settings getEntityRulesSettings(UMetadataClient client) {
    try {
      String settingsJson =
          client
              .getHttpClient()
              .executeForString(HttpMethod.GET, SETTINGS_ENDPOINT + "/entityRulesSettings", null);

      return MAPPER.readValue(settingsJson, Settings.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get entity rules settings", e);
    }
  }

  /**
   * Update system settings.
   *
   * @param client UMetadata client
   * @param settings Settings object to update
   * @return Updated Settings object
   */
  public static Settings updateSettings(UMetadataClient client, Settings settings) {
    try {
      String settingsJson = MAPPER.writeValueAsString(settings);
      String responseJson =
          client
              .getHttpClient()
              .executeForString(
                  HttpMethod.PUT,
                  SETTINGS_ENDPOINT,
                  settingsJson,
                  RequestOptions.builder().header("Content-Type", "application/json").build());

      return MAPPER.readValue(responseJson, Settings.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to update settings", e);
    }
  }

  /**
   * Check if a specific rule is currently enabled.
   *
   * @param client UMetadata client
   * @param ruleName name of the rule to check
   * @return true if rule is enabled, false otherwise
   */
  public static boolean isRuleEnabled(UMetadataClient client, String ruleName) {
    try {
      Settings settings = getEntityRulesSettings(client);
      EntityRulesSettings entityRulesSettings =
          MAPPER.convertValue(settings.getConfigValue(), EntityRulesSettings.class);

      if (entityRulesSettings.getEntitySemantics() != null) {
        for (var rule : entityRulesSettings.getEntitySemantics()) {
          if (ruleName.equals(rule.getName())) {
            return rule.getEnabled() != null && rule.getEnabled();
          }
        }
      }
      return false;
    } catch (Exception e) {
      throw new RuntimeException("Failed to check rule status: " + ruleName, e);
    }
  }
}
