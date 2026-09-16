package org.umetadata.mcp.tools;

import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.EntityInterface;
import org.umetadata.schema.type.ChangeEvent;
import org.umetadata.schema.type.EventType;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.formatter.util.FormatterUtil;

@Slf4j
public final class McpChangeEventUtil {
  private McpChangeEventUtil() {}

  public static <T extends EntityInterface> void publishChangeEvent(
      T entity, EventType changeType, String userName) {
    if (entity == null || changeType == null || changeType.equals(EventType.ENTITY_NO_CHANGE)) {
      return;
    }
    try {
      ChangeEvent changeEvent =
          FormatterUtil.createChangeEventForEntity(userName, changeType, entity);
      changeEvent.setUserName(userName);

      if (changeEvent.getEntity() != null) {
        Object rawEntity = changeEvent.getEntity();
        ChangeEvent copy =
            org.umetadata.service.events.ChangeEventHandler.copyChangeEvent(changeEvent);
        copy.setEntity(JsonUtils.pojoToMaskedJson(rawEntity));
        Entity.getCollectionDAO().changeEventDAO().insert(JsonUtils.pojoToJson(copy));
      } else {
        Entity.getCollectionDAO().changeEventDAO().insert(JsonUtils.pojoToJson(changeEvent));
      }

      LOG.debug(
          "Published MCP change event {}:{}:{}",
          changeEvent.getEntityId(),
          changeEvent.getEventType(),
          changeEvent.getEntityType());
    } catch (Exception e) {
      LOG.error("Failed to publish MCP change event for {}", entity.getId(), e);
    }
  }
}
