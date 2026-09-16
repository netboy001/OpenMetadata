package org.umetadata.service.search.indexes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.umetadata.common.utils.CommonUtil;
import org.umetadata.schema.ColumnsEntityInterface;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.type.Column;
import org.umetadata.schema.type.TagLabel;
import org.umetadata.service.search.models.FlattenColumn;
import org.umetadata.service.util.FullyQualifiedName;

public interface ColumnIndex extends SearchIndex {
  default void parseColumns(
      List<Column> columns, List<FlattenColumn> flattenColumns, String parentColumn) {
    Optional<String> optParentColumn =
        Optional.ofNullable(parentColumn).filter(Predicate.not(String::isEmpty));
    List<TagLabel> tags = new ArrayList<>();
    for (Column col : columns) {
      String columnName = col.getName();
      if (optParentColumn.isPresent()) {
        columnName = FullyQualifiedName.add(optParentColumn.get(), columnName);
      }
      if (col.getTags() != null) {
        tags = col.getTags();
      }

      FlattenColumn flattenColumn =
          FlattenColumn.builder().name(columnName).description(col.getDescription()).build();

      if (!tags.isEmpty()) {
        flattenColumn.setTags(tags);
      }
      flattenColumns.add(flattenColumn);
      if (col.getChildren() != null) {
        parseColumns(col.getChildren(), flattenColumns, col.getName());
      }
    }
  }

  default String getColumnDescriptionStatus(EntityInterface entity) {
    List<Class<?>> interfaces = Arrays.asList(entity.getClass().getInterfaces());
    if (interfaces.contains(ColumnsEntityInterface.class)) {
      for (Column col : ((ColumnsEntityInterface) entity).getColumns()) {
        if (CommonUtil.nullOrEmpty(col.getDescription())) {
          return "INCOMPLETE";
        }
      }
    }
    return "COMPLETE";
  }
}
