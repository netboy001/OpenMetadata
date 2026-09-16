package org.umetadata.service.search.indexes;

import java.util.Map;
import java.util.Set;
import org.umetadata.schema.entity.data.Database;
import org.umetadata.service.Entity;

public record DatabaseIndex(Database database) implements SearchIndex {

  @Override
  public Object getEntity() {
    return database;
  }

  @Override
  public Set<String> getExcludedFields() {
    return Set.of("databaseSchemas");
  }

  public Map<String, Object> buildSearchIndexDocInternal(Map<String, Object> doc) {
    Map<String, Object> commonAttributes = getCommonAttributesMap(database, Entity.DATABASE);
    doc.putAll(commonAttributes);
    return doc;
  }
}
