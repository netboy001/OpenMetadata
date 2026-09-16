package org.umetadata.sdk.services.feed;

import java.util.HashMap;
import java.util.Map;
import org.umetadata.schema.api.feed.ResolveTask;
import org.umetadata.schema.entity.feed.Thread;
import org.umetadata.schema.type.TaskStatus;
import org.umetadata.schema.utils.ResultList;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.network.RequestOptions;
import org.umetadata.sdk.services.EntityServiceBase;

public class FeedService extends EntityServiceBase<Thread> {
  private static final String BASE_PATH = "/v1/feed";

  public FeedService(HttpClient httpClient) {
    super(httpClient, BASE_PATH);
  }

  @Override
  protected Class<Thread> getEntityClass() {
    return Thread.class;
  }

  public static class ThreadList extends ResultList<Thread> {
    // Used for deserialization
  }

  public ResultList<Thread> listTasks(String entityLink, TaskStatus taskStatus, Integer limit) {
    Map<String, String> queryParams = new HashMap<>();
    if (entityLink != null) {
      queryParams.put("entityLink", entityLink);
    }
    if (taskStatus != null) {
      queryParams.put("taskStatus", taskStatus.toString());
      queryParams.put("type", "Task");
    }
    if (limit != null) {
      queryParams.put("limit", limit.toString());
    }

    RequestOptions requestOptions = RequestOptions.builder().queryParams(queryParams).build();

    return httpClient.execute(HttpMethod.GET, BASE_PATH, null, ThreadList.class, requestOptions);
  }

  public Thread resolveTask(String taskId, ResolveTask resolveTask) {
    return httpClient.execute(
        HttpMethod.PUT, BASE_PATH + "/tasks/" + taskId + "/resolve", resolveTask, Thread.class);
  }
}
