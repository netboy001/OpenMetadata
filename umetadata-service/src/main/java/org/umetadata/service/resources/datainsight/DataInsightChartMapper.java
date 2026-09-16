package org.umetadata.service.resources.datainsight;

import org.umetadata.schema.api.dataInsight.CreateDataInsightChart;
import org.umetadata.schema.dataInsight.DataInsightChart;
import org.umetadata.service.mapper.EntityMapper;

public class DataInsightChartMapper
    implements EntityMapper<DataInsightChart, CreateDataInsightChart> {
  @Override
  public DataInsightChart createToEntity(CreateDataInsightChart create, String user) {
    return copy(new DataInsightChart(), create, user)
        .withName(create.getName())
        .withDescription(create.getDescription())
        .withDataIndexType(create.getDataIndexType())
        .withDimensions(create.getDimensions())
        .withMetrics(create.getMetrics())
        .withDisplayName(create.getDisplayName());
  }
}
