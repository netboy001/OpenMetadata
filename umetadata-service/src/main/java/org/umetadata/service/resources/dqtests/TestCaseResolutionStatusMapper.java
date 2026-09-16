package org.umetadata.service.resources.dqtests;

import org.umetadata.schema.api.tests.CreateTestCaseResolutionStatus;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.schema.tests.TestCase;
import org.umetadata.schema.tests.type.TestCaseResolutionStatus;
import org.umetadata.schema.type.Include;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityTimeSeriesMapper;

public class TestCaseResolutionStatusMapper
    implements EntityTimeSeriesMapper<TestCaseResolutionStatus, CreateTestCaseResolutionStatus> {
  @Override
  public TestCaseResolutionStatus createToEntity(
      CreateTestCaseResolutionStatus create, String user) {
    TestCase testCaseEntity =
        Entity.getEntityByName(Entity.TEST_CASE, create.getTestCaseReference(), null, Include.ALL);
    User userEntity = Entity.getEntityByName(Entity.USER, user, null, Include.ALL);

    return new TestCaseResolutionStatus()
        .withTimestamp(System.currentTimeMillis())
        .withTestCaseResolutionStatusType(create.getTestCaseResolutionStatusType())
        .withTestCaseResolutionStatusDetails(create.getTestCaseResolutionStatusDetails())
        .withUpdatedBy(userEntity.getEntityReference())
        .withUpdatedAt(System.currentTimeMillis())
        .withTestCaseReference(testCaseEntity.getEntityReference())
        .withSeverity(create.getSeverity());
  }
}
