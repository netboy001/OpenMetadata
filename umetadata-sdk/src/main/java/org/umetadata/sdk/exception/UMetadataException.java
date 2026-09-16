package org.umetadata.sdk.exception;

public class UMetadataException extends Exception {
  private int statusCode;
  private String errorCode;

  public UMetadataException(String message) {
    super(message);
  }

  public UMetadataException(String message, Throwable cause) {
    super(message, cause);
  }

  public UMetadataException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public UMetadataException(int statusCode, String errorCode, String message) {
    super(message);
    this.statusCode = statusCode;
    this.errorCode = errorCode;
  }

  public UMetadataException(int statusCode, String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
    this.errorCode = errorCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
