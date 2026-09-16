package org.umetadata.service.exception;

import jakarta.ws.rs.core.Response;
import org.umetadata.sdk.exception.WebServiceException;

public class SystemSettingsException extends WebServiceException {
  private static final String ERROR_TYPE = "SYSTEM_SETTINGS_EXCEPTION";

  public SystemSettingsException(String message) {
    super(Response.Status.BAD_REQUEST, ERROR_TYPE, message);
  }
}
