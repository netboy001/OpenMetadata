package org.umetadata.service.secrets.converter;

import java.util.List;
import org.umetadata.schema.security.ssl.ValidateSSLClientConfig;
import org.umetadata.schema.services.connections.database.CockroachConnection;
import org.umetadata.schema.services.connections.database.common.basicAuth;
import org.umetadata.schema.utils.JsonUtils;

/**
 * Converter class to get an `CockroachConnection` object.
 */
public class CockroachConnectionClassConverter extends ClassConverter {

  private static final List<Class<?>> SSL_SOURCE_CLASS = List.of(ValidateSSLClientConfig.class);
  private static final List<Class<?>> CONFIG_SOURCE_CLASSES = List.of(basicAuth.class);

  public CockroachConnectionClassConverter() {
    super(CockroachConnection.class);
  }

  @Override
  public Object convert(Object object) {
    CockroachConnection cockroachConnection =
        (CockroachConnection) JsonUtils.convertValue(object, this.clazz);

    tryToConvert(cockroachConnection.getAuthType(), CONFIG_SOURCE_CLASSES)
        .ifPresent(cockroachConnection::setAuthType);

    tryToConvert(cockroachConnection.getSslConfig(), SSL_SOURCE_CLASS)
        .ifPresent(cockroachConnection::setSslConfig);

    return cockroachConnection;
  }
}
