package org.umetadata.schema.utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;
import org.umetadata.schema.api.UMetadataServerVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VersionUtils {
  private static final Logger LOG = LoggerFactory.getLogger(VersionUtils.class);

  private VersionUtils() {}

  public static UMetadataServerVersion getUMetadataServerVersion(String resourceName) {
    UMetadataServerVersion version = new UMetadataServerVersion();
    try {
      InputStream fileInput = VersionUtils.class.getResourceAsStream(resourceName);
      Properties props = new Properties();
      props.load(fileInput);
      version.setVersion(props.getProperty("version", "unknown"));
      version.setRevision(props.getProperty("revision", "unknown"));

      String timestampAsString = props.getProperty("timestamp");
      Long timestamp = timestampAsString != null ? Long.valueOf(timestampAsString) : null;
      version.setTimestamp(timestamp);
    } catch (Exception ie) {
      LOG.warn("Failed to read catalog version file");
    }
    return version;
  }

  public static String[] getVersionFromString(String input) {
    return input.split(Pattern.quote("."));
  }
}
