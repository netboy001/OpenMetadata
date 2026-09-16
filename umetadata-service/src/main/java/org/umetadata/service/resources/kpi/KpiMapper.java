package org.umetadata.service.resources.kpi;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.dataInsight.kpi.CreateKpiRequest;
import org.umetadata.schema.dataInsight.kpi.Kpi;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class KpiMapper implements EntityMapper<Kpi, CreateKpiRequest> {
  @Override
  public Kpi createToEntity(CreateKpiRequest create, String user) {
    return copy(new Kpi(), create, user)
        .withStartDate(create.getStartDate())
        .withEndDate(create.getEndDate())
        .withTargetValue(create.getTargetValue())
        .withDataInsightChart(
            getEntityReference(
                Entity.DATA_INSIGHT_CUSTOM_CHART, create.getDataInsightChart().value()))
        .withMetricType(create.getMetricType());
  }
}
