package org.umetadata.service.security.auth;

import static org.umetadata.service.exception.CatalogExceptionMessage.AUTHENTICATOR_OPERATION_NOT_SUPPORTED;
import static org.umetadata.service.exception.CatalogExceptionMessage.FORBIDDEN_AUTHENTICATOR_OP;

import jakarta.ws.rs.core.Response;
import org.umetadata.schema.auth.LoginRequest;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.auth.JwtResponse;
import org.umetadata.service.exception.CustomExceptionMessage;

public class NoopAuthenticator implements AuthenticatorHandler {
  @Override
  public void init(UMetadataApplicationConfig config) {
    /* deprecated unused */
  }

  @Override
  public JwtResponse loginUser(LoginRequest loginRequest) {
    throw new CustomExceptionMessage(
        Response.Status.FORBIDDEN,
        AUTHENTICATOR_OPERATION_NOT_SUPPORTED,
        FORBIDDEN_AUTHENTICATOR_OP);
  }

  @Override
  public void checkIfLoginBlocked(String userName) {
    throw new CustomExceptionMessage(
        Response.Status.FORBIDDEN,
        AUTHENTICATOR_OPERATION_NOT_SUPPORTED,
        FORBIDDEN_AUTHENTICATOR_OP);
  }

  @Override
  public void recordFailedLoginAttempt(String providedIdentity, String userName) {
    throw new CustomExceptionMessage(
        Response.Status.FORBIDDEN,
        AUTHENTICATOR_OPERATION_NOT_SUPPORTED,
        FORBIDDEN_AUTHENTICATOR_OP);
  }

  @Override
  public void validatePassword(String providedIdentity, String reqPassword, User storedUser) {
    throw new CustomExceptionMessage(
        Response.Status.FORBIDDEN,
        AUTHENTICATOR_OPERATION_NOT_SUPPORTED,
        FORBIDDEN_AUTHENTICATOR_OP);
  }

  @Override
  public User lookUserInProvider(String email, String pwd) {
    throw new CustomExceptionMessage(
        Response.Status.FORBIDDEN,
        AUTHENTICATOR_OPERATION_NOT_SUPPORTED,
        FORBIDDEN_AUTHENTICATOR_OP);
  }
}
