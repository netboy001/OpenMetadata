package org.umetadata.service.resources.metrics;

import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import org.umetadata.schema.api.data.CreateMetric;
import org.umetadata.schema.entity.data.Metric;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class MetricMapper implements EntityMapper<Metric, CreateMetric> {
  @Override
  public Metric createToEntity(CreateMetric create, String user) {
    return copy(new Metric(), create, user)
        .withMetricExpression(create.getMetricExpression())
        .withGranularity(create.getGranularity())
        .withRelatedMetrics(getEntityReferences(Entity.METRIC, create.getRelatedMetrics()))
        .withMetricType(create.getMetricType())
        .withUnitOfMeasurement(create.getUnitOfMeasurement())
        .withCustomUnitOfMeasurement(create.getCustomUnitOfMeasurement());
  }
}
