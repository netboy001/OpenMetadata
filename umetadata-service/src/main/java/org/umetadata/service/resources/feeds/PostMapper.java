package org.umetadata.service.resources.feeds;

import java.util.Collections;
import java.util.UUID;
import org.umetadata.schema.api.feed.CreatePost;
import org.umetadata.schema.type.Post;

public class PostMapper {
  public Post createToEntity(CreatePost create, String user) {
    return new Post()
        .withId(UUID.randomUUID())
        .withMessage(create.getMessage())
        .withFrom(create.getFrom())
        .withReactions(Collections.emptyList())
        .withPostTs(System.currentTimeMillis());
  }
}
