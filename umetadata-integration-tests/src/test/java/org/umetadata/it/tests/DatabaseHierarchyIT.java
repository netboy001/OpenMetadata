package org.umetadata.it.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.umetadata.it.factories.DatabaseSchemaTestFactory;
import org.umetadata.it.factories.DatabaseServiceTestFactory;
import org.umetadata.it.factories.DatabaseTestFactory;
import org.umetadata.it.factories.TableTestFactory;
import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.it.util.TestNamespaceExtension;
import org.umetadata.schema.entity.data.Database;
import org.umetadata.schema.entity.data.DatabaseSchema;
import org.umetadata.schema.entity.data.Table;
import org.umetadata.schema.entity.services.DatabaseService;
import org.umetadata.sdk.fluent.DatabaseSchemas;

@ExtendWith(TestNamespaceExtension.class)
public class DatabaseHierarchyIT {

  @BeforeAll
  static void setup() {
    // Initialize fluent APIs (triggers client creation which sets up all fluent APIs)
    SdkClients.adminClient();
  }

  @Test
  void createDatabaseSchemaTable(TestNamespace ns) {
    // Service - uses fluent API, no client parameter needed
    DatabaseService svc = DatabaseServiceTestFactory.create(ns, "Postgres");
    assertNotNull(svc.getId());

    // Database - uses fluent API
    Database db = DatabaseTestFactory.create(ns, svc.getFullyQualifiedName());
    assertNotNull(db.getId());

    // Schema - uses fluent API
    DatabaseSchema schema = DatabaseSchemaTestFactory.create(ns, db.getFullyQualifiedName());
    assertNotNull(schema.getId());

    // Update schema description using fluent API
    DatabaseSchemas.find(schema.getId().toString()).fetch().withDescription("updated").save();

    // Table - uses fluent API
    Table table = TableTestFactory.createSimple(ns, schema.getFullyQualifiedName());
    assertNotNull(table.getId());
  }
}
