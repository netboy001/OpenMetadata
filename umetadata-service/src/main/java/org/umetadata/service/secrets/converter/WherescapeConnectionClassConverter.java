package org.umetadata.service.secrets.converter;

import java.util.List;
import org.umetadata.schema.services.connections.database.MssqlConnection;
import org.umetadata.schema.services.connections.pipeline.WherescapeConnection;
import org.umetadata.schema.utils.JsonUtils;

/** Converter class to get an `SupersetConnection` object. */
public class WherescapeConnectionClassConverter extends ClassConverter {

  private static final List<Class<?>> CONNECTION_CLASSES = List.of(MssqlConnection.class);

  public WherescapeConnectionClassConverter() {
    super(WherescapeConnection.class);
  }

  @Override
  public Object convert(Object object) {
    WherescapeConnection wherescapeConnection =
        (WherescapeConnection) JsonUtils.convertValue(object, this.clazz);

    tryToConvertOrFail(wherescapeConnection.getDatabaseConnection(), CONNECTION_CLASSES)
        .ifPresent(wherescapeConnection::setDatabaseConnection);

    return wherescapeConnection;
  }
}
