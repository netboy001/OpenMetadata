package org.umetadata.service.exception;

import jakarta.ws.rs.core.Response;
import org.umetadata.sdk.exception.WebServiceException;

public class LimitsException extends WebServiceException {
  private static final String ERROR_TYPE = "LIMITS_EXCEPTION";

  public LimitsException(String message) {
    super(Response.Status.TOO_MANY_REQUESTS, ERROR_TYPE, message);
  }
}
