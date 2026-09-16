package org.umetadata.service.migration.utils.v1102;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.common.utils.CommonUtil;
import org.umetadata.schema.api.search.SearchSettings;
import org.umetadata.schema.settings.Settings;
import org.umetadata.schema.settings.SettingsType;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.EntityRepository;
import org.umetadata.service.jdbi3.SystemRepository;
import org.umetadata.service.util.EntityUtil;

@Slf4j
public class MigrationUtil {

  private static final SystemRepository systemRepository = Entity.getSystemRepository();
  private static final String SEARCH_SETTINGS_KEY = "searchSettings";

  public static void updateSearchSettingsNlqConfiguration() {
    try {
      LOG.info("Updating search settings nlqConfiguration for improved owner search (PR #23794)");

      Settings searchSettings = systemRepository.getConfigWithKey(SEARCH_SETTINGS_KEY);

      if (searchSettings == null) {
        LOG.warn("Search settings not found, loading from default file");
        loadDefaultSearchSettings();
        return;
      }

      SearchSettings currentSettings =
          JsonUtils.readValue(
              JsonUtils.pojoToJson(searchSettings.getConfigValue()), SearchSettings.class);

      SearchSettings defaultSettings = loadSearchSettingsFromFile();

      if (defaultSettings != null && defaultSettings.getNlqConfiguration() != null) {
        LOG.info("Updating nlqConfiguration with latest changes");
        currentSettings.setNlqConfiguration(defaultSettings.getNlqConfiguration());
        searchSettings.withConfigValue(currentSettings);
        systemRepository.updateSetting(searchSettings);
        LOG.info("Search settings nlqConfiguration updated successfully");
      } else {
        LOG.warn("Could not load default nlqConfiguration, skipping update");
      }

    } catch (Exception e) {
      LOG.error("Error updating search settings nlqConfiguration", e);
      throw new RuntimeException("Failed to update search settings nlqConfiguration", e);
    }
  }

  private static SearchSettings loadSearchSettingsFromFile() {
    try {
      List<String> jsonDataFiles =
          EntityUtil.getJsonDataResources(".*json/data/settings/searchSettings.json$");
      if (!jsonDataFiles.isEmpty()) {
        String json =
            CommonUtil.getResourceAsStream(
                EntityRepository.class.getClassLoader(), jsonDataFiles.getFirst());
        return JsonUtils.readValue(json, SearchSettings.class);
      }
    } catch (Exception e) {
      LOG.error("Failed to load default search settings from file", e);
    }
    return null;
  }

  private static void loadDefaultSearchSettings() {
    try {
      SearchSettings defaultSettings = loadSearchSettingsFromFile();
      if (defaultSettings != null) {
        Settings setting =
            new Settings()
                .withConfigType(SettingsType.SEARCH_SETTINGS)
                .withConfigValue(defaultSettings);
        Entity.getSystemRepository().createNewSetting(setting);
        LOG.info("Created new search settings from default file");
      }
    } catch (Exception e) {
      LOG.error("Failed to create default search settings", e);
    }
  }
}
