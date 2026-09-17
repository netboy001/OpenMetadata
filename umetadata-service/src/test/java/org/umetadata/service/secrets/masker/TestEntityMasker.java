package org.umetadata.service.secrets.masker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.umetadata.schema.api.services.DatabaseConnection;
import org.umetadata.schema.auth.JWTAuthMechanism;
import org.umetadata.schema.entity.automations.TestServiceConnectionRequest;
import org.umetadata.schema.entity.automations.Workflow;
import org.umetadata.schema.entity.services.ServiceType;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineType;
import org.umetadata.schema.entity.teams.AuthenticationMechanism;
import org.umetadata.schema.metadataIngestion.DbtPipeline;
import org.umetadata.schema.metadataIngestion.SourceConfig;
import org.umetadata.schema.metadataIngestion.dbtconfig.DbtGCSConfig;
import org.umetadata.schema.security.SecurityConfiguration;
import org.umetadata.schema.security.client.UMetadataJWTClientConfig;
import org.umetadata.schema.security.credentials.GCPCredentials;
import org.umetadata.schema.security.credentials.GCPValues;
import org.umetadata.schema.services.connections.dashboard.SupersetConnection;
import org.umetadata.schema.services.connections.database.BigQueryConnection;
import org.umetadata.schema.services.connections.database.DatalakeConnection;
import org.umetadata.schema.services.connections.database.MysqlConnection;
import org.umetadata.schema.services.connections.database.common.basicAuth;
import org.umetadata.schema.services.connections.database.datalake.GCSConfig;
import org.umetadata.schema.services.connections.metadata.UMetadataConnection;
import org.umetadata.schema.services.connections.pipeline.AirflowConnection;
import org.umetadata.schema.utils.JsonUtils;

abstract class TestEntityMasker {

  private static final String PASSWORD = "*********";

  protected static final SecurityConfiguration CONFIG = new SecurityConfiguration();

  public TestEntityMasker() {}

  @AfterAll
  static void afterAll() {
    EntityMaskerFactory.setEntityMasker(null);
  }

  @Test
  void testAirflowConnectionMasker() {
    AirflowConnection airflowConnection =
        new AirflowConnection().withConnection(buildMysqlConnection());
    AirflowConnection masked =
        (AirflowConnection)
            EntityMaskerFactory.createEntityMasker()
                .maskServiceConnectionConfig(airflowConnection, "Airflow", ServiceType.PIPELINE);
    assertNotNull(masked);
    assertEquals(
        (JsonUtils.convertValue(
                ((MysqlConnection) masked.getConnection()).getAuthType(), basicAuth.class)
            .getPassword()),
        getMaskedPassword());
    AirflowConnection unmasked =
        (AirflowConnection)
            EntityMaskerFactory.createEntityMasker()
                .unmaskServiceConnectionConfig(
                    masked, airflowConnection, "Airflow", ServiceType.PIPELINE);
    assertEquals(
        PASSWORD,
        JsonUtils.convertValue(
                ((MysqlConnection) unmasked.getConnection()).getAuthType(), basicAuth.class)
            .getPassword());
  }

  @Test
  void testBigQueryConnectionMasker() {
    BigQueryConnection bigQueryConnection =
        new BigQueryConnection().withCredentials(buildGcpCredentials());
    BigQueryConnection masked =
        (BigQueryConnection)
            EntityMaskerFactory.createEntityMasker()
                .maskServiceConnectionConfig(bigQueryConnection, "BigQuery", ServiceType.DATABASE);
    assertNotNull(masked);
    assertEquals(getPrivateKeyFromGcsConfig(masked.getCredentials()), getMaskedPassword());
    BigQueryConnection unmasked =
        (BigQueryConnection)
            EntityMaskerFactory.createEntityMasker()
                .unmaskServiceConnectionConfig(
                    masked, bigQueryConnection, "BigQuery", ServiceType.DATABASE);
    assertEquals(PASSWORD, getPrivateKeyFromGcsConfig(unmasked.getCredentials()));
  }

  @Test
  void testDatalakeConnectionMasker() {
    DatalakeConnection datalakeConnection =
        new DatalakeConnection().withConfigSource(buildGcsConfig());
    DatalakeConnection masked =
        (DatalakeConnection)
            EntityMaskerFactory.createEntityMasker()
                .maskServiceConnectionConfig(datalakeConnection, "Datalake", ServiceType.DATABASE);
    assertNotNull(masked);
    assertEquals(
        getPrivateKeyFromGcsConfig(((GCSConfig) masked.getConfigSource()).getSecurityConfig()),
        getMaskedPassword());
    DatalakeConnection unmasked =
        (DatalakeConnection)
            EntityMaskerFactory.createEntityMasker()
                .unmaskServiceConnectionConfig(
                    masked, datalakeConnection, "Datalake", ServiceType.DATABASE);
    assertEquals(
        PASSWORD,
        getPrivateKeyFromGcsConfig(((GCSConfig) unmasked.getConfigSource()).getSecurityConfig()));
  }

  @Test
  void testDbtPipelineMasker() {
    IngestionPipeline dbtPipeline = buildIngestionPipeline();
    IngestionPipeline originalDbtPipeline = buildIngestionPipeline();
    EntityMaskerFactory.createEntityMasker().maskIngestionPipeline(dbtPipeline);
    assertNotNull(dbtPipeline);
    assertEquals(
        getPrivateKeyFromGcsConfig(
            ((DbtGCSConfig)
                    ((DbtPipeline) dbtPipeline.getSourceConfig().getConfig()).getDbtConfigSource())
                .getDbtSecurityConfig()),
        getMaskedPassword());
    EntityMaskerFactory.createEntityMasker()
        .unmaskIngestionPipeline(dbtPipeline, originalDbtPipeline);
    assertEquals(
        PASSWORD,
        getPrivateKeyFromGcsConfig(
            ((DbtGCSConfig)
                    ((DbtPipeline) dbtPipeline.getSourceConfig().getConfig()).getDbtConfigSource())
                .getDbtSecurityConfig()));
  }

  @Test
  void testJWTAAuthenticationMechanismMasker() {
    AuthenticationMechanism authenticationMechanism = buildAuthenticationMechanism();
    AuthenticationMechanism originalSsoAuthenticationMechanism = buildAuthenticationMechanism();
    EntityMaskerFactory.createEntityMasker()
        .maskAuthenticationMechanism("test", authenticationMechanism);
    assertInstanceOf(JWTAuthMechanism.class, authenticationMechanism.getConfig());
    EntityMaskerFactory.createEntityMasker()
        .unmaskAuthenticationMechanism(
            "test", authenticationMechanism, originalSsoAuthenticationMechanism);
    assertInstanceOf(JWTAuthMechanism.class, authenticationMechanism.getConfig());
  }

  @Test
  void testSupersetConnectionMasker() {
    SupersetConnection supersetConnection =
        new SupersetConnection().withConnection(buildMysqlConnection());
    SupersetConnection masked =
        (SupersetConnection)
            EntityMaskerFactory.createEntityMasker()
                .maskServiceConnectionConfig(supersetConnection, "Superset", ServiceType.DASHBOARD);
    assertNotNull(masked);
    assertEquals(
        JsonUtils.convertValue(
                ((MysqlConnection) masked.getConnection()).getAuthType(), basicAuth.class)
            .getPassword(),
        getMaskedPassword());
    SupersetConnection unmasked =
        (SupersetConnection)
            EntityMaskerFactory.createEntityMasker()
                .unmaskServiceConnectionConfig(
                    masked, supersetConnection, "Superset", ServiceType.DASHBOARD);
    assertEquals(
        PASSWORD,
        JsonUtils.convertValue(
                ((MysqlConnection) unmasked.getConnection()).getAuthType(), basicAuth.class)
            .getPassword());
  }

  @Test
  void testWorkflowMasker() {
    Workflow workflow =
        new Workflow()
            .withRequest(
                new TestServiceConnectionRequest()
                    .withConnection(new DatabaseConnection().withConfig(buildMysqlConnection()))
                    .withServiceType(ServiceType.DATABASE)
                    .withConnectionType("Mysql"))
            .withuMetadataServerConnection(buildUMetadataConnection());
    Workflow masked = EntityMaskerFactory.createEntityMasker().maskWorkflow(workflow);
    assertNotNull(masked);
    assertEquals(
        JsonUtils.convertValue(
                ((MysqlConnection)
                        ((DatabaseConnection)
                                ((TestServiceConnectionRequest) masked.getRequest())
                                    .getConnection())
                            .getConfig())
                    .getAuthType(),
                basicAuth.class)
            .getPassword(),
        getMaskedPassword());
    Workflow unmasked = EntityMaskerFactory.createEntityMasker().unmaskWorkflow(masked, workflow);
    assertEquals(
        PASSWORD,
        JsonUtils.convertValue(
                ((MysqlConnection)
                        ((DatabaseConnection)
                                ((TestServiceConnectionRequest) unmasked.getRequest())
                                    .getConnection())
                            .getConfig())
                    .getAuthType(),
                basicAuth.class)
            .getPassword());
  }

  @Test
  void testObjectMaskerWithoutACustomClassConverter() {
    MysqlConnection mysqlConnection = buildMysqlConnection();
    MysqlConnection masked =
        (MysqlConnection)
            EntityMaskerFactory.createEntityMasker()
                .maskServiceConnectionConfig(mysqlConnection, "Mysql", ServiceType.DATABASE);
    assertNotNull(masked);
    assertEquals(
        JsonUtils.convertValue(masked.getAuthType(), basicAuth.class).getPassword(),
        getMaskedPassword());
    MysqlConnection unmasked =
        (MysqlConnection)
            EntityMaskerFactory.createEntityMasker()
                .unmaskServiceConnectionConfig(
                    masked, mysqlConnection, "Mysql", ServiceType.DATABASE);
    assertEquals(
        PASSWORD, JsonUtils.convertValue(unmasked.getAuthType(), basicAuth.class).getPassword());
  }

  protected String getMaskedPassword() {
    return PASSWORD;
  }

  private GCPCredentials buildGcpCredentials() {
    return new GCPCredentials().withGcpConfig(new GCPValues().withPrivateKey(PASSWORD));
  }

  private MysqlConnection buildMysqlConnection() {
    return new MysqlConnection().withAuthType(new basicAuth().withPassword(PASSWORD));
  }

  private GCSConfig buildGcsConfig() {
    return new GCSConfig().withSecurityConfig(buildGcpCredentials());
  }

  private String getPrivateKeyFromGcsConfig(GCPCredentials masked) {
    return ((GCPValues) masked.getGcpConfig()).getPrivateKey();
  }

  private IngestionPipeline buildIngestionPipeline() {
    return new IngestionPipeline()
        .withPipelineType(PipelineType.DBT)
        .withSourceConfig(
            new SourceConfig()
                .withConfig(
                    new DbtPipeline()
                        .withDbtConfigSource(
                            new DbtGCSConfig().withDbtSecurityConfig(buildGcpCredentials()))))
        .withuMetadataServerConnection(buildUMetadataConnection());
  }

  private UMetadataConnection buildUMetadataConnection() {
    return new UMetadataConnection()
        .withSecurityConfig(new UMetadataJWTClientConfig().withJwtToken(PASSWORD));
  }

  private AuthenticationMechanism buildAuthenticationMechanism() {
    return new AuthenticationMechanism()
        .withAuthType(AuthenticationMechanism.AuthType.JWT)
        .withConfig(new JWTAuthMechanism().withJWTToken(PASSWORD));
  }
}
