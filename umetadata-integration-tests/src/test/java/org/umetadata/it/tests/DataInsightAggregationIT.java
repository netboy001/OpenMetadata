package org.umetadata.it.tests;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.it.util.TestNamespaceExtension;
import org.umetadata.schema.api.data.CreateDatabaseSchema;
import org.umetadata.schema.api.data.CreateGlossary;
import org.umetadata.schema.api.data.CreateGlossaryTerm;
import org.umetadata.schema.api.data.CreateTable;
import org.umetadata.schema.entity.data.Database;
import org.umetadata.schema.entity.data.DatabaseSchema;
import org.umetadata.schema.entity.data.Glossary;
import org.umetadata.schema.entity.data.GlossaryTerm;
import org.umetadata.schema.entity.data.Table;
import org.umetadata.schema.entity.services.DatabaseService;
import org.umetadata.schema.type.Column;
import org.umetadata.schema.type.ColumnDataType;
import org.umetadata.schema.type.TagLabel;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * Integration tests for Data Insight aggregation behavior.
 *
 * <p>Tests that aggregations on tables with many tags do not cause bucket explosion errors in
 * OpenSearch/Elasticsearch. The bucket explosion occurs when nested terms aggregations create more
 * buckets than the max_buckets limit (65,535 by default).
 *
 * <p>Related fix: Reduced aggregation .size(1000) to .size(100) to limit max buckets to 10,000.
 */
@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(TestNamespaceExtension.class)
public class DataInsightAggregationIT {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final int TAG_COUNT = 150;

  private DatabaseService dbService;
  private Database database;
  private DatabaseSchema schema;
  private Glossary glossary;

  @Test
  void testTableWithManyTagsDoesNotCauseBucketExplosion(TestNamespace ns) throws Exception {
    UMetadataClient client = SdkClients.adminClient();

    initializeTestEntities(ns);

    List<GlossaryTerm> glossaryTerms = createGlossaryTerms(ns, TAG_COUNT);

    List<Column> columns = createColumnsWithTags(glossaryTerms);
    Table table = createTableWithTaggedColumns(ns, "bucket_test_table", columns);

    assertNotNull(table);
    assertTrue(table.getColumns().size() >= TAG_COUNT);

    assertDoesNotThrow(
        () -> {
          String response =
              client
                  .search()
                  .query("*")
                  .index("table_search_index")
                  .includeAggregations()
                  .execute();

          assertNotNull(response);
          JsonNode root = OBJECT_MAPPER.readTree(response);
          assertFalse(
              root.has("error") && root.get("error").asText().contains("too_many_buckets"),
              "Should not have too_many_buckets_exception error");
        },
        "Search with aggregations should not throw bucket explosion error");
  }

  @Test
  void testAggregateOnTagFieldWithManyTagsSucceeds(TestNamespace ns) throws Exception {
    UMetadataClient client = SdkClients.adminClient();

    initializeTestEntities(ns);

    List<GlossaryTerm> glossaryTerms = createGlossaryTerms(ns, TAG_COUNT);

    List<Column> columns = createColumnsWithTags(glossaryTerms);
    Table table = createTableWithTaggedColumns(ns, "agg_tag_test", columns);

    assertNotNull(table);

    assertDoesNotThrow(
        () -> {
          String response =
              client
                  .search()
                  .aggregate("*")
                  .index("table_search_index")
                  .field("tags.tagFQN")
                  .execute();

          assertNotNull(response);
          JsonNode root = OBJECT_MAPPER.readTree(response);
          assertFalse(
              root.has("error") && root.get("error").asText().contains("too_many_buckets"),
              "Aggregation on tags.tagFQN should not cause bucket explosion");
        },
        "Aggregate query on tag field should succeed without bucket explosion");
  }

  private synchronized void initializeTestEntities(TestNamespace ns) {
    if (dbService != null) {
      return;
    }

    UMetadataClient client = SdkClients.adminClient();
    String shortId = ns.shortPrefix();

    org.umetadata.schema.services.connections.database.PostgresConnection conn =
        org.umetadata.sdk.fluent.DatabaseServices.postgresConnection()
            .hostPort("localhost:5432")
            .username("test")
            .build();

    dbService =
        org.umetadata.sdk.fluent.DatabaseServices.builder()
            .name("di_agg_svc_" + shortId)
            .connection(conn)
            .description("Test service for data insight aggregation")
            .create();

    org.umetadata.schema.api.data.CreateDatabase dbReq =
        new org.umetadata.schema.api.data.CreateDatabase();
    dbReq.setName("di_agg_db_" + shortId);
    dbReq.setService(dbService.getFullyQualifiedName());
    database = client.databases().create(dbReq);

    CreateDatabaseSchema schemaReq = new CreateDatabaseSchema();
    schemaReq.setName("di_agg_schema_" + shortId);
    schemaReq.setDatabase(database.getFullyQualifiedName());
    schema = client.databaseSchemas().create(schemaReq);

    CreateGlossary glossaryReq = new CreateGlossary();
    glossaryReq.setName("BucketTestGlossary_" + shortId);
    glossaryReq.setDescription("Glossary for bucket explosion testing");
    glossary = client.glossaries().create(glossaryReq);
  }

  private List<GlossaryTerm> createGlossaryTerms(TestNamespace ns, int count) {
    UMetadataClient client = SdkClients.adminClient();
    List<GlossaryTerm> terms = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      CreateGlossaryTerm termReq = new CreateGlossaryTerm();
      termReq.setName("Term_" + i);
      termReq.setDisplayName("Term " + i);
      termReq.setDescription("Test glossary term " + i);
      termReq.setGlossary(glossary.getFullyQualifiedName());

      GlossaryTerm term = client.glossaryTerms().create(termReq);
      terms.add(term);
    }

    return terms;
  }

  private List<Column> createColumnsWithTags(List<GlossaryTerm> glossaryTerms) {
    List<Column> columns = new ArrayList<>();

    for (int i = 0; i < glossaryTerms.size(); i++) {
      GlossaryTerm term = glossaryTerms.get(i);

      TagLabel tagLabel =
          new TagLabel()
              .withTagFQN(term.getFullyQualifiedName())
              .withName(term.getName())
              .withSource(TagLabel.TagSource.GLOSSARY)
              .withLabelType(TagLabel.LabelType.MANUAL)
              .withState(TagLabel.State.CONFIRMED);

      Column column =
          new Column()
              .withName("column_" + i)
              .withDataType(ColumnDataType.VARCHAR)
              .withDataLength(255)
              .withTags(List.of(tagLabel));

      columns.add(column);
    }

    return columns;
  }

  private Table createTableWithTaggedColumns(TestNamespace ns, String name, List<Column> columns) {
    UMetadataClient client = SdkClients.adminClient();

    CreateTable tableReq = new CreateTable();
    tableReq.setName(ns.prefix(name));
    tableReq.setDatabaseSchema(schema.getFullyQualifiedName());
    tableReq.setColumns(columns);

    return client.tables().create(tableReq);
  }
}
