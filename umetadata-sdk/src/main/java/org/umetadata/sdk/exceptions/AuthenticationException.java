package org.umetadata.sdk.exceptions;

public class AuthenticationException extends UMetadataException {
  public AuthenticationException(String message) {
    super(message, 401);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause, 401, null);
  }
}
