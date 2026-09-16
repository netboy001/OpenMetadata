package org.umetadata.service.resources.query;

import org.umetadata.schema.entity.data.CreateQueryCostRecord;
import org.umetadata.schema.entity.data.Query;
import org.umetadata.schema.entity.data.QueryCostRecord;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.schema.type.Include;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityTimeSeriesMapper;

public class QueryCostRecordMapper
    implements EntityTimeSeriesMapper<QueryCostRecord, CreateQueryCostRecord> {
  @Override
  public QueryCostRecord createToEntity(CreateQueryCostRecord create, String user) {
    Query query =
        Entity.getEntity(Entity.QUERY, create.getQueryReference().getId(), null, Include.ALL);
    User userEntity = Entity.getEntityByName(Entity.USER, user, null, Include.ALL);

    return new QueryCostRecord()
        .withTimestamp(create.getTimestamp())
        .withCost(create.getCost())
        .withCount(create.getCount())
        .withTotalDuration(create.getTotalDuration())
        .withUpdatedAt(System.currentTimeMillis())
        .withUpdatedBy(userEntity.getEntityReference())
        .withQueryReference(query.getEntityReference());
  }
}
