package org.umetadata.service.jobs;

import org.umetadata.schema.jobs.BackgroundJob;

public interface JobHandler {
  void runJob(BackgroundJob job) throws BackgroundJobException;

  boolean sendStatusToWebSocket();
}
