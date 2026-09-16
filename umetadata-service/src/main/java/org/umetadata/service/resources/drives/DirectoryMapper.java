package org.umetadata.service.resources.drives;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateDirectory;
import org.umetadata.schema.entity.data.Directory;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class DirectoryMapper implements EntityMapper<Directory, CreateDirectory> {
  @Override
  public Directory createToEntity(CreateDirectory create, String user) {
    return copy(new Directory(), create, user)
        .withService(getEntityReference(Entity.DRIVE_SERVICE, create.getService()))
        .withParent(
            create.getParent() != null
                ? getEntityReference(Entity.DIRECTORY, create.getParent())
                : null)
        .withPath(create.getPath())
        .withIsShared(create.getIsShared())
        .withSourceUrl(create.getSourceUrl())
        .withDirectoryType(create.getDirectoryType());
  }
}
