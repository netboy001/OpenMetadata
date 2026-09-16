package org.umetadata.service.pipelineService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.api.configuration.pipelineServiceClient.PipelineServiceClientConfiguration;
import org.umetadata.schema.entity.app.App;
import org.umetadata.schema.entity.app.AppMarketPlaceDefinition;
import org.umetadata.schema.entity.automations.Workflow;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineServiceClientResponse;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineStatus;
import org.umetadata.service.clients.pipeline.PipelineServiceClient;

public class MockPipelineServiceClient extends PipelineServiceClient {

  public MockPipelineServiceClient(
      PipelineServiceClientConfiguration pipelineServiceClientConfiguration) {
    super(pipelineServiceClientConfiguration);
  }

  private PipelineServiceClientResponse createSuccessResponse(String message) {
    return new PipelineServiceClientResponse()
        .withCode(200)
        .withReason(message)
        .withPlatform("Mock");
  }

  @Override
  public PipelineServiceClientResponse getServiceStatusInternal() {
    return createSuccessResponse("Mock service is healthy");
  }

  @Override
  public PipelineServiceClientResponse runAutomationsWorkflow(Workflow workflow) {
    return createSuccessResponse("Mock workflow triggered successfully");
  }

  @Override
  public PipelineServiceClientResponse runApplicationFlow(App application) {
    return createSuccessResponse("Mock application flow triggered successfully");
  }

  @Override
  public PipelineServiceClientResponse validateAppRegistration(AppMarketPlaceDefinition app) {
    return createSuccessResponse("Mock app validation successful");
  }

  @Override
  public PipelineServiceClientResponse deployPipeline(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service) {
    return createSuccessResponse("Mock pipeline deployed successfully");
  }

  @Override
  public PipelineServiceClientResponse runPipeline(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service) {
    return createSuccessResponse("Mock pipeline triggered successfully");
  }

  @Override
  public PipelineServiceClientResponse deletePipeline(IngestionPipeline ingestionPipeline) {
    return createSuccessResponse("Mock pipeline deleted successfully");
  }

  @Override
  public List<PipelineStatus> getQueuedPipelineStatusInternal(IngestionPipeline ingestionPipeline) {
    return new ArrayList<>();
  }

  @Override
  public PipelineServiceClientResponse toggleIngestion(IngestionPipeline ingestionPipeline) {
    return createSuccessResponse("Mock pipeline toggled successfully");
  }

  @Override
  public Map<String, String> getLastIngestionLogs(
      IngestionPipeline ingestionPipeline, String after) {
    return new HashMap<>();
  }

  @Override
  public PipelineServiceClientResponse killIngestion(IngestionPipeline ingestionPipeline) {
    return createSuccessResponse("Mock pipeline killed successfully");
  }

  @Override
  public Map<String, String> requestGetHostIp() {
    Map<String, String> result = new HashMap<>();
    result.put("ip", "127.0.0.1");
    return result;
  }
}
