package org.umetadata.service.resources.dqtests;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.tests.CreateTestCase;
import org.umetadata.schema.tests.TestCase;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.resources.feeds.MessageParser;

public class TestCaseMapper implements EntityMapper<TestCase, CreateTestCase> {
  @Override
  public TestCase createToEntity(CreateTestCase create, String user) {
    MessageParser.EntityLink entityLink = MessageParser.EntityLink.parse(create.getEntityLink());
    return copy(new TestCase(), create, user)
        .withDescription(create.getDescription())
        .withName(create.getName())
        .withDisplayName(create.getDisplayName())
        .withParameterValues(create.getParameterValues())
        .withEntityLink(create.getEntityLink())
        .withComputePassedFailedRowCount(create.getComputePassedFailedRowCount())
        .withUseDynamicAssertion(create.getUseDynamicAssertion())
        .withDimensionColumns(create.getDimensionColumns())
        .withEntityFQN(entityLink.getFullyQualifiedFieldValue())
        .withTestDefinition(getEntityReference(Entity.TEST_DEFINITION, create.getTestDefinition()))
        .withTags(create.getTags())
        .withCreatedBy(user);
  }
}
