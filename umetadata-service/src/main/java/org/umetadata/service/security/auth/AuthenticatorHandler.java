package org.umetadata.service.security.auth;

import static org.umetadata.common.utils.CommonUtil.nullOrEmpty;
import static org.umetadata.service.exception.CatalogExceptionMessage.NOT_IMPLEMENTED_METHOD;
import static org.umetadata.service.util.UserUtil.getRoleListFromUser;

import freemarker.template.TemplateException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.UUID;
import org.umetadata.schema.api.teams.CreateUser;
import org.umetadata.schema.auth.ChangePasswordRequest;
import org.umetadata.schema.auth.JWTAuthMechanism;
import org.umetadata.schema.auth.LoginRequest;
import org.umetadata.schema.auth.PasswordResetRequest;
import org.umetadata.schema.auth.RefreshToken;
import org.umetadata.schema.auth.RegistrationRequest;
import org.umetadata.schema.auth.ServiceTokenType;
import org.umetadata.schema.auth.TokenRefreshRequest;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.auth.JwtResponse;
import org.umetadata.service.exception.CustomExceptionMessage;
import org.umetadata.service.security.jwt.JWTTokenGenerator;

public interface AuthenticatorHandler {
  String NOT_IMPLEMENTED_ERROR_TYPE = "NOT_IMPLEMENTED";

  void init(UMetadataApplicationConfig config);

  /**
   * Reload the authentication configuration without restarting the server
   * @param config New authentication configuration
   */
  default void reload(UMetadataApplicationConfig config) {
    // Default implementation reinitializes
    init(config);
  }

  JwtResponse loginUser(LoginRequest loginRequest) throws IOException, TemplateException;

  void checkIfLoginBlocked(String userName);

  void recordFailedLoginAttempt(String email, String userName)
      throws TemplateException, IOException;

  void validatePassword(String providedIdentity, String reqPassword, User omUser)
      throws TemplateException, IOException;

  User lookUserInProvider(String email, String pwd) throws TemplateException, IOException;

  default User registerUser(RegistrationRequest registrationRequest) {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void sendEmailVerification(UriInfo uriInfo, User user) throws IOException {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void confirmEmailRegistration(UriInfo uriInfo, String emailToken) {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void resendRegistrationToken(UriInfo uriInfo, User registeredUser) throws IOException {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void sendPasswordResetLink(
      UriInfo uriInfo, User user, String subject, String templateFilePath) throws IOException {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void resetUserPasswordWithToken(UriInfo uriInfo, PasswordResetRequest req)
      throws IOException {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void changeUserPwdWithOldPwd(UriInfo uriInfo, String userName, ChangePasswordRequest req)
      throws IOException {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default RefreshToken createRefreshTokenForLogin(UUID currentUserId) {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default JwtResponse getNewAccessToken(TokenRefreshRequest request) {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default void sendInviteMailToUser(
      UriInfo uriInfo,
      User user,
      String subject,
      CreateUser.CreatePasswordType requestType,
      String pwd)
      throws IOException {
    throw new CustomExceptionMessage(
        Response.Status.NOT_IMPLEMENTED, NOT_IMPLEMENTED_ERROR_TYPE, NOT_IMPLEMENTED_METHOD);
  }

  default JwtResponse getJwtResponse(User storedUser, long expireInSeconds) {
    RefreshToken refreshToken = createRefreshTokenForLogin(storedUser.getId());
    JWTAuthMechanism jwtAuthMechanism =
        JWTTokenGenerator.getInstance()
            .generateJWTToken(
                storedUser.getName(),
                getRoleListFromUser(storedUser),
                !nullOrEmpty(storedUser.getIsAdmin()) && storedUser.getIsAdmin(),
                storedUser.getEmail(),
                expireInSeconds,
                false,
                ServiceTokenType.OM_USER);

    JwtResponse response = new JwtResponse();
    response.setTokenType("Bearer");
    response.setAccessToken(jwtAuthMechanism.getJWTToken());
    response.setRefreshToken(refreshToken.getToken().toString());
    response.setExpiryDuration(jwtAuthMechanism.getJWTTokenExpiresAt());
    return response;
  }
}
