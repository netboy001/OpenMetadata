package org.umetadata.service.resources.dqtests;

import static org.umetadata.service.Entity.TEST_CASE;

import java.util.UUID;
import org.umetadata.schema.api.tests.CreateTestCaseResult;
import org.umetadata.schema.tests.TestCase;
import org.umetadata.schema.tests.type.TestCaseResult;
import org.umetadata.schema.type.Include;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityTimeSeriesMapper;
import org.umetadata.service.util.RestUtil;

public class TestCaseResultMapper
    implements EntityTimeSeriesMapper<TestCaseResult, CreateTestCaseResult> {
  @Override
  public TestCaseResult createToEntity(CreateTestCaseResult create, String user) {
    TestCase testCase = Entity.getEntityByName(TEST_CASE, create.getFqn(), "", Include.ALL);
    RestUtil.validateTimestampMilliseconds(create.getTimestamp());
    return new TestCaseResult()
        .withId(UUID.randomUUID())
        .withTestCaseFQN(testCase.getFullyQualifiedName())
        .withTimestamp(create.getTimestamp())
        .withTestCaseStatus(create.getTestCaseStatus())
        .withResult(create.getResult())
        .withSampleData(create.getSampleData())
        .withTestResultValue(create.getTestResultValue())
        .withPassedRows(create.getPassedRows())
        .withFailedRows(create.getFailedRows())
        .withPassedRowsPercentage(create.getPassedRowsPercentage())
        .withFailedRowsPercentage(create.getFailedRowsPercentage())
        .withIncidentId(create.getIncidentId())
        .withMaxBound(create.getMaxBound())
        .withMinBound(create.getMinBound())
        .withDimensionResults(create.getDimensionResults());
  }
}
