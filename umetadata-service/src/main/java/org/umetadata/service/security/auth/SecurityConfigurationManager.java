/*
 *  Copyright 2025 Collate
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.umetadata.service.security.auth;

import static org.umetadata.schema.settings.SettingsType.AUTHENTICATION_CONFIGURATION;
import static org.umetadata.schema.settings.SettingsType.AUTHORIZER_CONFIGURATION;
import static org.umetadata.schema.settings.SettingsType.MCP_CONFIGURATION;

import io.dropwizard.core.setup.Environment;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.api.configuration.MCPConfiguration;
import org.umetadata.schema.api.security.AuthenticationConfiguration;
import org.umetadata.schema.api.security.AuthorizerConfiguration;
import org.umetadata.schema.api.security.ClientType;
import org.umetadata.schema.configuration.SecurityConfiguration;
import org.umetadata.schema.services.connections.metadata.AuthProvider;
import org.umetadata.service.Entity;
import org.umetadata.service.UMetadataApplication;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.exception.AuthenticationException;
import org.umetadata.service.resources.settings.SettingsCache;

@Slf4j
public class SecurityConfigurationManager {

  @FunctionalInterface
  public interface ConfigurationChangeListener {
    void onConfigurationChanged(
        AuthenticationConfiguration authConfig,
        AuthorizerConfiguration authzConfig,
        MCPConfiguration mcpConfig);
  }

  private static class Holder {
    private static final SecurityConfigurationManager INSTANCE = new SecurityConfigurationManager();
  }

  private volatile AuthenticationConfiguration currentAuthConfig;
  private volatile AuthorizerConfiguration currentAuthzConfig;
  private volatile MCPConfiguration currentMcpConfig;
  private final List<ConfigurationChangeListener> listeners = new CopyOnWriteArrayList<>();

  public void setCurrentAuthConfig(AuthenticationConfiguration authConfig) {
    this.currentAuthConfig = authConfig;
  }

  public void setCurrentAuthzConfig(AuthorizerConfiguration authzConfig) {
    this.currentAuthzConfig = authzConfig;
  }

  public void setCurrentMcpConfig(MCPConfiguration mcpConfig) {
    this.currentMcpConfig = mcpConfig;
  }

  private SecurityConfiguration previousSecurityConfig;
  private MCPConfiguration previousMcpConfig;
  private UMetadataApplication application;
  private Environment environment;
  private UMetadataApplicationConfig config;
  @Getter private AuthenticatorHandler authenticatorHandler;

  private SecurityConfigurationManager() {}

  public static SecurityConfigurationManager getInstance() {
    return Holder.INSTANCE;
  }

  public static AuthenticationConfiguration getCurrentAuthConfig() {
    return getInstance().currentAuthConfig;
  }

  public static AuthorizerConfiguration getCurrentAuthzConfig() {
    return getInstance().currentAuthzConfig;
  }

  public static MCPConfiguration getCurrentMcpConfig() {
    return getInstance().currentMcpConfig;
  }

  public void setAuthenticatorHandler(AuthenticatorHandler handler) {
    this.authenticatorHandler = handler;
  }

  public void initialize(
      UMetadataApplication app, UMetadataApplicationConfig config, Environment env) {
    application = app;
    environment = env;
    this.config = config;

    try {
      currentAuthConfig =
          SettingsCache.getSetting(AUTHENTICATION_CONFIGURATION, AuthenticationConfiguration.class);
      currentAuthzConfig =
          SettingsCache.getSetting(AUTHORIZER_CONFIGURATION, AuthorizerConfiguration.class);
      LOG.info(
          "Loaded security configuration from database - provider: {}",
          currentAuthConfig != null ? currentAuthConfig.getProvider() : "null");
    } catch (Exception e) {
      LOG.warn(
          "Failed to load configuration from database, falling back to YAML: {}", e.getMessage());
      currentAuthConfig = config.getAuthenticationConfiguration();
      currentAuthzConfig = config.getAuthorizerConfiguration();
      LOG.info(
          "Using security configuration from YAML - provider: {}",
          currentAuthConfig != null ? currentAuthConfig.getProvider() : "null");
    }

    // MCP config is optional — load separately so its absence doesn't affect auth config
    currentMcpConfig =
        SettingsCache.getSettingOrDefault(
            MCP_CONFIGURATION, config.getMcpConfiguration(), MCPConfiguration.class);
  }

  public SecurityConfiguration getCurrentSecurityConfig() {
    // Apply LDAP default values before returning to prevent JSON PATCH errors
    // when updating fields that were previously null in the database
    if (currentAuthConfig != null && currentAuthConfig.getLdapConfiguration() != null) {
      Entity.getSystemRepository()
          .ensureLdapConfigDefaultValues(currentAuthConfig.getLdapConfiguration());
    }

    return new SecurityConfiguration()
        .withAuthenticationConfiguration(currentAuthConfig)
        .withAuthorizerConfiguration(currentAuthzConfig);
  }

  public void reloadSecuritySystem() {
    try {
      previousSecurityConfig = getCurrentSecurityConfig();
      previousMcpConfig = currentMcpConfig;
      currentAuthConfig =
          SettingsCache.getSetting(AUTHENTICATION_CONFIGURATION, AuthenticationConfiguration.class);
      currentAuthzConfig =
          SettingsCache.getSetting(AUTHORIZER_CONFIGURATION, AuthorizerConfiguration.class);
      currentMcpConfig =
          SettingsCache.getSettingOrDefault(MCP_CONFIGURATION, null, MCPConfiguration.class);

      UMetadataApplicationConfig appConfig = this.config;
      appConfig.setAuthenticationConfiguration(currentAuthConfig);
      appConfig.setAuthorizerConfiguration(currentAuthzConfig);
      if (currentMcpConfig != null) {
        appConfig.setMcpConfiguration(currentMcpConfig);
      }

      application.reinitializeAuthSystem(appConfig, environment);

      notifyListeners();

      LOG.info("Successfully reloaded security system with new configuration");
    } catch (Exception e) {
      LOG.error("Failed to reload security system", e);
      rollbackConfiguration();
      throw new AuthenticationException("Failed to reload security system", e);
    }
  }

  public void addConfigurationChangeListener(ConfigurationChangeListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
      LOG.debug(
          "Registered configuration change listener: {}", listener.getClass().getSimpleName());
    }
  }

  public void removeConfigurationChangeListener(ConfigurationChangeListener listener) {
    if (listeners.remove(listener)) {
      LOG.debug("Removed configuration change listener: {}", listener.getClass().getSimpleName());
    }
  }

  private void notifyListeners() {
    for (ConfigurationChangeListener listener : listeners) {
      try {
        listener.onConfigurationChanged(currentAuthConfig, currentAuthzConfig, currentMcpConfig);
        LOG.debug(
            "Notified configuration change listener: {}", listener.getClass().getSimpleName());
      } catch (Exception e) {
        LOG.error(
            "Error notifying configuration change listener: {}",
            listener.getClass().getSimpleName(),
            e);
      }
    }
  }

  private void rollbackConfiguration() {
    if (previousSecurityConfig != null) {
      currentAuthConfig = previousSecurityConfig.getAuthenticationConfiguration();
      currentAuthzConfig = previousSecurityConfig.getAuthorizerConfiguration();
      currentMcpConfig = previousMcpConfig;
      LOG.info("Rolled back to previous security configuration");
    }
  }

  public static boolean isSaml() {
    AuthenticationConfiguration authConfig = getCurrentAuthConfig();
    return authConfig != null && AuthProvider.SAML.equals(authConfig.getProvider());
  }

  public static boolean isBasicAuth() {
    AuthenticationConfiguration authConfig = getCurrentAuthConfig();
    return authConfig != null && AuthProvider.BASIC.equals(authConfig.getProvider());
  }

  public static boolean isLdap() {
    AuthenticationConfiguration authConfig = getCurrentAuthConfig();
    return authConfig != null && AuthProvider.LDAP.equals(authConfig.getProvider());
  }

  public static boolean isOidc() {
    AuthenticationConfiguration authConfig = getCurrentAuthConfig();
    if (authConfig == null) {
      return false;
    }
    AuthProvider provider = authConfig.getProvider();
    return provider == AuthProvider.GOOGLE
        || provider == AuthProvider.OKTA
        || provider == AuthProvider.AUTH_0
        || provider == AuthProvider.AZURE
        || provider == AuthProvider.CUSTOM_OIDC
        || provider == AuthProvider.AWS_COGNITO;
  }

  public static boolean isConfidentialClient() {
    AuthenticationConfiguration authConfig = getCurrentAuthConfig();
    return authConfig != null && ClientType.CONFIDENTIAL.equals(authConfig.getClientType());
  }
}
