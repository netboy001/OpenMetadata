/*
 *  Copyright 2021 Collate
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

package org.umetadata.client.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import feign.Feign;
import feign.RequestTemplate;
import feign.form.FormEncoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.client.ApiClient;
import org.umetadata.client.api.SystemApi;
import org.umetadata.client.security.factory.AuthenticationProviderFactory;
import org.umetadata.schema.api.UMetadataServerVersion;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.schema.utils.VersionUtils;

@Slf4j
public class UMetadata {
  private static final UMetadataServerVersion UMETADATA_VERSION_CLIENT;

  static {
    UMETADATA_VERSION_CLIENT = VersionUtils.getUMetadataServerVersion("/catalog/VERSION");
  }

  private ApiClient apiClient;
  private static final String REQUEST_INTERCEPTOR_KEY = "custom";

  public UMetadata(UMetadataConnection config) {
    initClient(config);
    validateVersion();
  }

  public UMetadata(UMetadataConnection config, boolean validateVersion) {
    initClient(config);
    if (validateVersion) validateVersion();
  }

  public void initClient(UMetadataConnection config) {
    apiClient = new ApiClient();
    Feign.Builder builder =
        Feign.builder()
            .encoder(new FormEncoder(new JacksonEncoder(apiClient.getObjectMapper())))
            .decoder(new JacksonDecoder(apiClient.getObjectMapper()))
            .logger(new Slf4jLogger())
            .client(new OkHttpClient());
    initClient(config, builder);
  }

  public void initClient(UMetadataConnection config, Feign.Builder builder) {
    if (Objects.isNull(apiClient)) {
      apiClient = new ApiClient();
    }

    if (config.getExtraHeaders() != null
        && config.getExtraHeaders().getAdditionalProperties() != null
        && !config.getExtraHeaders().getAdditionalProperties().isEmpty()) {
      builder.requestInterceptor(
          requestTemplate ->
              applyExtraHeaders(
                  requestTemplate, config.getExtraHeaders().getAdditionalProperties()));
    }

    apiClient.setFeignBuilder(builder);
    AuthenticationProviderFactory factory = new AuthenticationProviderFactory();
    apiClient.addAuthorization("oauth", factory.getAuthProvider(config));
    String basePath = config.getHostPort() + "/";
    apiClient.setBasePath(basePath);
    apiClient.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  public <T extends ApiClient.Api> T buildClient(Class<T> clientClass) {
    return apiClient.buildClient(clientClass);
  }

  public void validateVersion() {
    String[] clientVersion = getClientVersion();
    String[] serverVersion = getServerVersion();
    // MAJOR MINOR REVISION
    if (serverVersion[0].equals(clientVersion[0])
        && serverVersion[1].equals(clientVersion[1])
        && serverVersion[2].equals(clientVersion[2])) {
      LOG.debug("UMetaData Client Initialized successfully.");
    } else {
      LOG.error(
          "UMetaData Client Failed to be Initialized successfully. Version mismatch between CLient and Server issue");
    }
  }

  public String[] getServerVersion() {
    SystemApi api = apiClient.buildClient(SystemApi.class);
    org.umetadata.client.model.UMetadataServerVersion serverVersion = api.getCatalogVersion();
    return VersionUtils.getVersionFromString(serverVersion.getVersion());
  }

  public String[] getClientVersion() {
    return VersionUtils.getVersionFromString(UMETADATA_VERSION_CLIENT.getVersion());
  }

  private void applyExtraHeaders(RequestTemplate template, Map<String, String> extraHeaders) {
    for (Map.Entry<String, String> entry : extraHeaders.entrySet()) {
      String headerValue = entry.getValue() != null ? entry.getValue() : "";
      template.header(entry.getKey(), headerValue);
      LOG.debug("Applied extra header: {} = {}", entry.getKey(), headerValue);
    }
  }
}
