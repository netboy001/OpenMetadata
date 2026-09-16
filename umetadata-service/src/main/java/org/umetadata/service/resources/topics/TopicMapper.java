package org.umetadata.service.resources.topics;

import static org.umetadata.service.util.EntityUtil.getEntityReference;

import org.umetadata.schema.api.data.CreateTopic;
import org.umetadata.schema.entity.data.Topic;
import org.umetadata.service.Entity;
import org.umetadata.service.mapper.EntityMapper;

public class TopicMapper implements EntityMapper<Topic, CreateTopic> {
  @Override
  public Topic createToEntity(CreateTopic create, String user) {
    return copy(new Topic(), create, user)
        .withService(getEntityReference(Entity.MESSAGING_SERVICE, create.getService()))
        .withPartitions(create.getPartitions())
        .withMessageSchema(create.getMessageSchema())
        .withCleanupPolicies(create.getCleanupPolicies())
        .withMaximumMessageSize(create.getMaximumMessageSize())
        .withMinimumInSyncReplicas(create.getMinimumInSyncReplicas())
        .withRetentionSize(create.getRetentionSize())
        .withRetentionTime(create.getRetentionTime())
        .withReplicationFactor(create.getReplicationFactor())
        .withTopicConfig(create.getTopicConfig())
        .withSourceUrl(create.getSourceUrl())
        .withSourceHash(create.getSourceHash());
  }
}
