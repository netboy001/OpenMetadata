package org.umetadata.service.secrets.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.umetadata.schema.auth.SSOAuthMechanism;
import org.umetadata.schema.entity.automations.TestServiceConnectionRequest;
import org.umetadata.schema.entity.automations.Workflow;
import org.umetadata.schema.metadataIngestion.DbtPipeline;
import org.umetadata.schema.metadataIngestion.dbtconfig.DbtGCSConfig;
import org.umetadata.schema.security.credentials.GCPCredentials;
import org.umetadata.schema.services.connections.dashboard.LookerConnection;
import org.umetadata.schema.services.connections.dashboard.SupersetConnection;
import org.umetadata.schema.services.connections.dashboard.TableauConnection;
import org.umetadata.schema.services.connections.database.BigQueryConnection;
import org.umetadata.schema.services.connections.database.DatalakeConnection;
import org.umetadata.schema.services.connections.database.IcebergConnection;
import org.umetadata.schema.services.connections.database.MysqlConnection;
import org.umetadata.schema.services.connections.database.PostgresConnection;
import org.umetadata.schema.services.connections.database.SalesforceConnection;
import org.umetadata.schema.services.connections.database.TrinoConnection;
import org.umetadata.schema.services.connections.database.datalake.GCSConfig;
import org.umetadata.schema.services.connections.pipeline.AirflowConnection;
import org.umetadata.schema.services.connections.pipeline.MatillionConnection;
import org.umetadata.schema.services.connections.search.ElasticSearchConnection;
import org.umetadata.schema.services.connections.storage.GCSConnection;

public class ClassConverterFactoryTest {

  @ParameterizedTest
  @ValueSource(
      classes = {
        AirflowConnection.class,
        BigQueryConnection.class,
        DatalakeConnection.class,
        MysqlConnection.class,
        PostgresConnection.class,
        DbtGCSConfig.class,
        DbtPipeline.class,
        GCSConfig.class,
        GCSConnection.class,
        ElasticSearchConnection.class,
        LookerConnection.class,
        SSOAuthMechanism.class,
        SupersetConnection.class,
        GCPCredentials.class,
        TableauConnection.class,
        TestServiceConnectionRequest.class,
        TrinoConnection.class,
        Workflow.class,
        SalesforceConnection.class,
        IcebergConnection.class,
        MatillionConnection.class,
      })
  void testClassConverterIsSet(Class<?> clazz) {
    assertFalse(
        ClassConverterFactory.getConverter(clazz) instanceof DefaultConnectionClassConverter);
  }

  @Test
  void testClassConvertedMapIsNotModified() {
    assertEquals(34, ClassConverterFactory.getConverterMap().size());
  }
}
