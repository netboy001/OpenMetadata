package org.umetadata.service.resources.dqtests;

import org.umetadata.schema.api.tests.CreateTestSuite;
import org.umetadata.schema.entity.data.Table;
import org.umetadata.schema.tests.TestSuite;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class TestSuiteMapper implements EntityMapper<TestSuite, CreateTestSuite> {
  @Override
  public TestSuite createToEntity(CreateTestSuite create, String user) {
    TestSuite testSuite =
        copy(new TestSuite(), create, user)
            .withDescription(create.getDescription())
            .withDisplayName(create.getDisplayName())
            .withDataContract(create.getDataContract())
            .withName(create.getName());
    if (create.getBasicEntityReference() != null) {
      Table table =
          Entity.getEntityByName(Entity.TABLE, create.getBasicEntityReference(), null, null);
      EntityReference entityReference =
          new EntityReference()
              .withId(table.getId())
              .withFullyQualifiedName(table.getFullyQualifiedName())
              .withName(table.getName())
              .withType(Entity.TABLE);
      testSuite.setBasicEntityReference(entityReference);
    }
    return testSuite;
  }
}
