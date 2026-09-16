package org.umetadata.service.resources.drives;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateWorksheet;
import org.umetadata.schema.entity.data.Worksheet;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class WorksheetMapper implements EntityMapper<Worksheet, CreateWorksheet> {
  @Override
  public Worksheet createToEntity(CreateWorksheet create, String user) {
    return copy(new Worksheet(), create, user)
        .withService(getEntityReference(Entity.DRIVE_SERVICE, create.getService()))
        .withSpreadsheet(getEntityReference(Entity.SPREADSHEET, create.getSpreadsheet()))
        .withWorksheetId(create.getWorksheetId())
        .withIndex(create.getIndex())
        .withRowCount(create.getRowCount())
        .withColumnCount(create.getColumnCount())
        .withColumns(create.getColumns())
        .withIsHidden(create.getIsHidden())
        .withSourceUrl(create.getSourceUrl());
  }
}
