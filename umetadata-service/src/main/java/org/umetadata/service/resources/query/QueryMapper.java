package org.umetadata.service.resources.query;

import static org.umetadata.service.Entity.USER;
import static org.umetadata.service.util.EntityUtil.getEntityReference;
import static org.umetadata.service.util.EntityUtil.getEntityReferences;

import org.umetadata.schema.api.data.CreateQuery;
import org.umetadata.schema.entity.data.Query;
import org.umetadata.schema.type.Votes;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.util.EntityUtil;

public class QueryMapper implements EntityMapper<Query, CreateQuery> {
  @Override
  public Query createToEntity(CreateQuery create, String user) {

    return copy(new Query(), create, user)
        .withQuery(create.getQuery())
        .withChecksum(EntityUtil.hash(create.getQuery()))
        .withService(getEntityReference(Entity.DATABASE_SERVICE, create.getService()))
        .withDuration(create.getDuration())
        .withVotes(new Votes().withUpVotes(0).withDownVotes(0))
        .withUsers(getEntityReferences(USER, create.getUsers()))
        .withQueryUsedIn(EntityUtil.validateAndPopulateEntityReferences(create.getQueryUsedIn()))
        .withQueryDate(create.getQueryDate())
        .withUsedBy(create.getUsedBy())
        .withTriggeredBy(create.getTriggeredBy())
        .withProcessedLineage(create.getProcessedLineage());
  }
}
