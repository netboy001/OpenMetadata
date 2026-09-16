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

package org.umetadata.service;

import java.util.Objects;

public final class UMetadataApplicationConfigHolder {

  private static volatile UMetadataApplicationConfig instance;

  private UMetadataApplicationConfigHolder() {
    throw new UnsupportedOperationException("Utility class cannot be instantiated");
  }

  public static void initialize(UMetadataApplicationConfig config) {
    Objects.requireNonNull(config, "UMetadataApplicationConfig cannot be null");
    if (instance != null) {
      throw new IllegalStateException("UMetadataApplicationConfig has already been initialized");
    }
    synchronized (UMetadataApplicationConfigHolder.class) {
      if (instance != null) {
        throw new IllegalStateException(
            "UMetadataApplicationConfig has already been initialized");
      }
      instance = config;
    }
  }

  public static UMetadataApplicationConfig getInstance() {
    UMetadataApplicationConfig result = instance;
    if (result == null) {
      throw new IllegalStateException(
          "UMetadataApplicationConfig has not been initialized. Call initialize() first.");
    }
    return result;
  }

  public static boolean isInitialized() {
    return instance != null;
  }
}
