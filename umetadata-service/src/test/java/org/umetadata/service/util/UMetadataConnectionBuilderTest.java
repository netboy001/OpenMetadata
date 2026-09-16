package org.umetadata.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.umetadata.schema.api.configuration.pipelineServiceClient.PipelineServiceClientConfiguration;
import org.umetadata.schema.entity.applications.configuration.ApplicationConfig;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineType;
import org.umetadata.schema.metadataIngestion.ApplicationPipeline;
import org.umetadata.schema.metadataIngestion.SourceConfig;
import org.umetadata.schema.security.secrets.SecretsManagerClientLoader;
import org.umetadata.schema.security.secrets.SecretsManagerConfiguration;
import org.umetadata.schema.security.secrets.SecretsManagerProvider;
import org.umetadata.schema.security.ssl.VerifySSL;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.service.Entity;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.UMetadataApplicationTest;
import org.umetadata.service.secrets.SecretsManagerFactory;

@Slf4j
public class UMetadataConnectionBuilderTest extends UMetadataApplicationTest {

  private static SecretsManagerConfiguration config;
  static final String CLUSTER_NAME = "test";

  @BeforeAll
  static void setUp() {
    config = new SecretsManagerConfiguration();
    config.setSecretsManager(SecretsManagerProvider.DB);
    SecretsManagerFactory.createSecretsManager(config, CLUSTER_NAME);
  }

  @Test
  void testUMetadataConnectionBuilder() {

    UMetadataApplicationConfig uMetadataApplicationConfig =
        new UMetadataApplicationConfig();
    uMetadataApplicationConfig.setClusterName(CLUSTER_NAME);
    uMetadataApplicationConfig.setPipelineServiceClientConfiguration(
        new PipelineServiceClientConfiguration()
            .withMetadataApiEndpoint("http://localhost:8585/api")
            .withVerifySSL(VerifySSL.NO_SSL)
            .withSecretsManagerLoader(SecretsManagerClientLoader.ENV));

    String botName =
        "autoClassification-bot"; // Whichever bot other than the ingestion-bot, which is the
    // default
    UMetadataConnection uMetadataServerConnection =
        new UMetadataConnectionBuilder(uMetadataApplicationConfig, botName).build();

    // The OM Connection passes the right JWT based on the incoming bot
    DecodedJWT jwt = JWT.decode(uMetadataServerConnection.getSecurityConfig().getJwtToken());
    Assertions.assertEquals("autoclassification-bot", jwt.getClaim("sub").asString());
  }

  @Test
  void testGetBotFromPipeline_Metadata() throws Exception {
    IngestionPipeline pipeline = new IngestionPipeline().withPipelineType(PipelineType.METADATA);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals(Entity.INGESTION_BOT_NAME, botName);
  }

  @Test
  void testGetBotFromPipeline_DBT() throws Exception {
    IngestionPipeline pipeline = new IngestionPipeline().withPipelineType(PipelineType.DBT);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals(Entity.INGESTION_BOT_NAME, botName);
  }

  @Test
  void testGetBotFromPipeline_AutoClassification() throws Exception {
    IngestionPipeline pipeline =
        new IngestionPipeline().withPipelineType(PipelineType.AUTO_CLASSIFICATION);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("autoClassification-bot", botName);
  }

  @Test
  void testGetBotFromPipeline_Profiler() throws Exception {
    IngestionPipeline pipeline = new IngestionPipeline().withPipelineType(PipelineType.PROFILER);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("profiler-bot", botName);
  }

  @Test
  void testGetBotFromPipeline_Lineage() throws Exception {
    IngestionPipeline pipeline = new IngestionPipeline().withPipelineType(PipelineType.LINEAGE);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("lineage-bot", botName);
  }

  @Test
  void testGetBotFromPipeline_Usage() throws Exception {
    IngestionPipeline pipeline = new IngestionPipeline().withPipelineType(PipelineType.USAGE);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("usage-bot", botName);
  }

  @Test
  void testGetBotFromPipeline_TestSuite() throws Exception {
    IngestionPipeline pipeline = new IngestionPipeline().withPipelineType(PipelineType.TEST_SUITE);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("testsuite-bot", botName);
  }

  @Test
  void testGetBotFromPipeline_DataInsight() throws Exception {
    IngestionPipeline pipeline =
        new IngestionPipeline().withPipelineType(PipelineType.DATA_INSIGHT);
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("datainsight-bot", botName);
  }

  @Test
  void testGetBotFromPipeline_Application() throws Exception {
    ApplicationConfig appConfig = new ApplicationConfig();
    appConfig.setAdditionalProperty("type", "SearchIndexing");
    IngestionPipeline pipeline =
        new IngestionPipeline()
            .withPipelineType(PipelineType.APPLICATION)
            .withSourceConfig(
                new SourceConfig()
                    .withConfig(
                        new ApplicationPipeline()
                            .withType(ApplicationPipeline.ApplicationConfigType.APPLICATION)
                            .withAppConfig(appConfig)));
    String botName = invokeBotFromPipeline(pipeline);
    Assertions.assertEquals("SearchIndexingApplicationBot", botName);
  }

  private String invokeBotFromPipeline(IngestionPipeline pipeline) throws Exception {
    UMetadataConnectionBuilder builder =
        new UMetadataConnectionBuilder(createTestConfig(), Entity.INGESTION_BOT_NAME);
    Method method =
        UMetadataConnectionBuilder.class.getDeclaredMethod(
            "getBotFromPipeline", IngestionPipeline.class);
    method.setAccessible(true);
    return (String) method.invoke(builder, pipeline);
  }

  private UMetadataApplicationConfig createTestConfig() {
    UMetadataApplicationConfig config = new UMetadataApplicationConfig();
    config.setClusterName(CLUSTER_NAME);
    config.setPipelineServiceClientConfiguration(
        new PipelineServiceClientConfiguration()
            .withMetadataApiEndpoint("http://localhost:8585/api")
            .withVerifySSL(VerifySSL.NO_SSL)
            .withSecretsManagerLoader(SecretsManagerClientLoader.ENV));
    return config;
  }
}
