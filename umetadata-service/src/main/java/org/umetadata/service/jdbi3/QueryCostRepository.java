package org.umetadata.service.jdbi3;

import java.io.IOException;
import org.umetadata.schema.entity.data.QueryCostRecord;
import org.umetadata.schema.entity.data.QueryCostSearchResult;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.query.QueryCostResource;

public class QueryCostRepository extends EntityTimeSeriesRepository<QueryCostRecord> {

  public QueryCostRepository() {
    super(
        QueryCostResource.COLLECTION_PATH,
        Entity.getCollectionDAO().queryCostRecordTimeSeriesDAO(),
        QueryCostRecord.class,
        Entity.QUERY_COST_RECORD);
  }

  public QueryCostSearchResult getQueryCostAggData(String serviceName) throws IOException {
    return searchRepository.getQueryCostRecords(serviceName);
  }
}
