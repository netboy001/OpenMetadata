package org.umetadata.service.resources.drives;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateFile;
import org.umetadata.schema.entity.data.File;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class FileMapper implements EntityMapper<File, CreateFile> {
  @Override
  public File createToEntity(CreateFile create, String user) {
    return copy(new File(), create, user)
        .withService(getEntityReference(Entity.DRIVE_SERVICE, create.getService()))
        .withDirectory(getEntityReference(Entity.DIRECTORY, create.getDirectory()))
        .withFileType(create.getFileType())
        .withMimeType(create.getMimeType())
        .withFileExtension(create.getFileExtension())
        .withPath(create.getPath())
        .withSize(create.getSize())
        .withColumns(create.getColumns())
        .withChecksum(create.getChecksum())
        .withWebViewLink(create.getWebViewLink())
        .withDownloadLink(create.getDownloadLink())
        .withIsShared(create.getIsShared())
        .withFileVersion(create.getFileVersion())
        .withSourceUrl(create.getSourceUrl());
  }
}
