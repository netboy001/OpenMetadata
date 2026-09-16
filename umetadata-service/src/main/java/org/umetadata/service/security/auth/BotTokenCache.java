package org.umetadata.service.security.auth;

import static org.umetadata.schema.type.Include.NON_DELETED;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.auth.JWTAuthMechanism;
import org.umetadata.schema.entity.teams.AuthenticationMechanism;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.UserRepository;
import org.umetadata.service.resources.teams.UserResource;
import org.umetadata.service.secrets.SecretsManager;
import org.umetadata.service.secrets.SecretsManagerFactory;
import org.umetadata.service.util.EntityUtil.Fields;

@Slf4j
public class BotTokenCache {
  public static final String EMPTY_STRING = "";
  private static final LoadingCache<String, String> BOTS_TOKEN_CACHE =
      CacheBuilder.newBuilder()
          .maximumSize(1000)
          .expireAfterWrite(2, TimeUnit.MINUTES)
          .build(new BotTokenLoader());

  private BotTokenCache() {
    // Private constructor for utility class
  }

  public static String getToken(String botName) {
    try {
      if (BOTS_TOKEN_CACHE.get(botName).equals(EMPTY_STRING)) {
        BOTS_TOKEN_CACHE.invalidate(botName);
      }
      return BOTS_TOKEN_CACHE.get(botName);
    } catch (ExecutionException | UncheckedExecutionException ex) {
      return null;
    }
  }

  public static void invalidateToken(String botName) {
    try {
      BOTS_TOKEN_CACHE.invalidate(botName);
    } catch (Exception ex) {
      LOG.error("Failed to invalidate Bot token cache for Bot {}", botName, ex);
    }
  }

  static class BotTokenLoader extends CacheLoader<String, String> {
    @Override
    public String load(@CheckForNull String botName) {
      UserRepository userRepository = (UserRepository) Entity.getEntityRepository(Entity.USER);
      User user =
          userRepository.getByName(
              null,
              botName,
              new Fields(Set.of(UserResource.USER_PROTECTED_FIELDS)),
              NON_DELETED,
              true);
      AuthenticationMechanism authenticationMechanism = user.getAuthenticationMechanism();
      SecretsManager secretsManager = SecretsManagerFactory.getSecretsManager();
      secretsManager.decryptAuthenticationMechanism(user.getName(), authenticationMechanism);
      if (authenticationMechanism != null) {
        JWTAuthMechanism jwtAuthMechanism =
            JsonUtils.convertValue(authenticationMechanism.getConfig(), JWTAuthMechanism.class);
        return jwtAuthMechanism.getJWTToken();
      }
      return null;
    }
  }
}
