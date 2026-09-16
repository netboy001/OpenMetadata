package org.umetadata.service.resources.dqtests;

import org.umetadata.schema.api.tests.CreateTestDefinition;
import org.umetadata.schema.tests.TestDefinition;
import org.umetadata.service.mapper.EntityMapper;

public class TestDefinitionMapper implements EntityMapper<TestDefinition, CreateTestDefinition> {
  @Override
  public TestDefinition createToEntity(CreateTestDefinition create, String user) {
    return copy(new TestDefinition(), create, user)
        .withDescription(create.getDescription())
        .withEntityType(create.getEntityType())
        .withTestPlatforms(create.getTestPlatforms())
        .withSupportedDataTypes(create.getSupportedDataTypes())
        .withSupportedServices(create.getSupportedServices())
        .withDisplayName(create.getDisplayName())
        .withParameterDefinition(create.getParameterDefinition())
        .withName(create.getName())
        .withSqlExpression(create.getSqlExpression())
        .withValidatorClass(create.getValidatorClass())
        .withDataQualityDimension(create.getDataQualityDimension());
  }
}
