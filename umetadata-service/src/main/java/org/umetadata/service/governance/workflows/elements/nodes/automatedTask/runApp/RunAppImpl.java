package org.umetadata.service.governance.workflows.elements.nodes.automatedTask.runApp;

import static org.umetadata.common.utils.CommonUtil.nullOrEmpty;
import static org.umetadata.service.util.EntityUtil.Fields.EMPTY_FIELDS;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.entity.app.App;
import org.umetadata.schema.entity.app.AppRunRecord;
import org.umetadata.schema.entity.app.AppType;
import org.umetadata.schema.entity.app.external.CollateAIAppConfig;
import org.umetadata.schema.entity.app.internal.CollateAIQualityAgentAppConfig;
import org.umetadata.schema.entity.app.internal.CollateAITierAgentAppConfig;
import org.umetadata.schema.entity.applications.configuration.internal.AppAnalyticsConfig;
import org.umetadata.schema.entity.applications.configuration.internal.BackfillConfiguration;
import org.umetadata.schema.entity.applications.configuration.internal.CostAnalysisConfig;
import org.umetadata.schema.entity.applications.configuration.internal.DataAssetsConfig;
import org.umetadata.schema.entity.applications.configuration.internal.DataInsightsAppConfig;
import org.umetadata.schema.entity.applications.configuration.internal.DataQualityConfig;
import org.umetadata.schema.entity.applications.configuration.internal.ModuleConfiguration;
import org.umetadata.schema.entity.applications.configuration.internal.ServiceFilter;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineStatus;
import org.umetadata.schema.entity.services.ingestionPipelines.PipelineStatusType;
import org.umetadata.schema.exception.JsonParsingException;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.sdk.PipelineServiceClientInterface;
import org.umetadata.service.Entity;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.apps.ApplicationHandler;
import org.umetadata.service.exception.EntityNotFoundException;
import org.umetadata.service.exception.UnhandledServerException;
import org.umetadata.service.jdbi3.AppRepository;
import org.umetadata.service.jdbi3.IngestionPipelineRepository;
import org.umetadata.service.resources.feeds.MessageParser;
import org.umetadata.service.util.EntityUtil;
import org.umetadata.service.util.UMetadataConnectionBuilder;

@Slf4j
public class RunAppImpl {
  public boolean execute(
      PipelineServiceClientInterface pipelineServiceClient,
      String appName,
      boolean waitForCompletion,
      long timeoutSeconds,
      MessageParser.EntityLink entityLink) {
    boolean wasSuccessful = true;
    ServiceEntityInterface service = Entity.getEntity(entityLink, "owners", Include.NON_DELETED);

    AppRepository appRepository = (AppRepository) Entity.getEntityRepository(Entity.APPLICATION);
    App app;
    try {
      app =
          appRepository.getByName(null, appName, new EntityUtil.Fields(Set.of("bot", "pipelines")));
    } catch (EntityNotFoundException ex) {
      LOG.warn(String.format("App: '%s' is not Installed. Skipping", appName));
      return wasSuccessful;
    }

    if (!validateAppShouldRun(app, service)) {
      return wasSuccessful;
    }

    long startTime = System.currentTimeMillis();
    long timeoutMillis = timeoutSeconds * 1000;

    Map<String, Object> config = getConfig(app, service);

    LOG.info(
        "[GovernanceWorkflows] '{}' running for '{}'", app.getDisplayName(), service.getName());
    if (app.getAppType().equals(AppType.Internal)) {
      wasSuccessful =
          runApp(appRepository, app, config, waitForCompletion, startTime, timeoutMillis);
    } else {
      App updatedApp = JsonUtils.deepCopy(app, App.class);
      updatedApp.setAppConfiguration(config);
      wasSuccessful =
          runApp(pipelineServiceClient, updatedApp, waitForCompletion, startTime, timeoutMillis);
      deployIngestionPipeline(pipelineServiceClient, app);
    }

    if (!wasSuccessful) {
      LOG.warn(
          "[GovernanceWorkflows] '{}' failed for '{}'", app.getDisplayName(), service.getName());
    }
    return wasSuccessful;
  }

  private boolean validateAppShouldRun(App app, ServiceEntityInterface service) {
    // We only want to run the CollateAIApplication and CollateAIQualityAgentApplication for
    // Databases
    if (Entity.getEntityTypeFromObject(service).equals(Entity.DATABASE_SERVICE)
        && List.of("CollateAIApplication", "CollateAIQualityAgentApplication")
            .contains(app.getName())) {
      return true;
    } else
      return List.of("DataInsightsApplication", "CollateAITierAgentApplication")
          .contains(app.getName());
  }

  private String getTableServiceFilter(String serviceName) {
    return String.format(
        "{\"query\":{\"bool\":{\"must\":[{\"bool\":{\"must\":[{\"term\":{\"entityType\":\"table\"}},{\"term\":{\"service.displayName.keyword\":\"%s\"}}]}}]}}}",
        serviceName);
  }

  private Map<String, Object> getConfig(App app, ServiceEntityInterface service) {
    Object config = JsonUtils.deepCopy(app.getAppConfiguration(), Object.class);

    switch (app.getName()) {
      case "CollateAIApplication" -> config =
          (JsonUtils.convertValue(config, CollateAIAppConfig.class))
              .withFilter(getTableServiceFilter(service.getName()))
              .withPatchIfEmpty(true);
      case "CollateAIQualityAgentApplication" -> config =
          (JsonUtils.convertValue(config, CollateAIQualityAgentAppConfig.class))
              .withFilter(getTableServiceFilter(service.getName()));
      case "CollateAITierAgentApplication" -> config =
          (JsonUtils.convertValue(config, CollateAITierAgentAppConfig.class))
              .withFilter(getTableServiceFilter(service.getName()))
              .withPatchIfEmpty(true);
      case "DataInsightsApplication" -> {
        DataInsightsAppConfig updatedAppConfig =
            (JsonUtils.convertValue(config, DataInsightsAppConfig.class));
        ModuleConfiguration updatedModuleConfig =
            updatedAppConfig
                .getModuleConfiguration()
                .withAppAnalytics(new AppAnalyticsConfig().withEnabled(false))
                .withCostAnalysis(new CostAnalysisConfig().withEnabled(false))
                .withDataQuality(new DataQualityConfig().withEnabled(false))
                .withDataAssets(
                    new DataAssetsConfig()
                        .withRetention(
                            updatedAppConfig
                                .getModuleConfiguration()
                                .getDataAssets()
                                .getRetention())
                        .withServiceFilter(
                            new ServiceFilter()
                                .withServiceName(service.getName())
                                .withServiceType(Entity.getEntityTypeFromObject(service))));

        config =
            updatedAppConfig
                .withBackfillConfiguration(new BackfillConfiguration().withEnabled(false))
                .withRecreateDataAssetsIndex(false)
                .withModuleConfiguration(updatedModuleConfig);
      }
    }
    return JsonUtils.getMap(config);
  }

  // Internal App Logic
  @SneakyThrows
  private boolean runApp(
      AppRepository repository,
      App app,
      Map<String, Object> config,
      boolean waitForCompletion,
      long startTime,
      long timeoutMillis) {
    int maxRetries = 5;
    int attempt = 0;
    long initialBackoffMillis = 10000; // 10 second
    long maxBackoffMillis = 60000; // 60 seconds

    while (attempt < maxRetries) {
      try {
        ApplicationHandler.getInstance()
            .triggerApplicationOnDemand(
                app, Entity.getCollectionDAO(), Entity.getSearchRepository(), config);
        break;
      } catch (JsonParsingException | UnhandledServerException e) {
        if (e.getMessage().contains("Job is already running")) {
          attempt++;
          if (attempt >= maxRetries) {
            LOG.error("Failed to run app after {} retries: {}", maxRetries, e.getMessage());
            return false;
          }

          long backoffMillis =
              Math.min(initialBackoffMillis * (long) Math.pow(2, attempt - 1), maxBackoffMillis);
          LOG.warn(
              "App is already running. Retrying in {} ms (attempt {}/{})",
              backoffMillis,
              attempt,
              maxRetries);

          try {
            Thread.sleep(backoffMillis);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", ie);
          }
        } else {
          throw e;
        }
      }
    }

    if (waitForCompletion) {
      return waitForCompletion(repository, app, startTime, timeoutMillis);
    } else {
      return true;
    }
  }

  private boolean waitForCompletion(
      AppRepository repository, App app, long startTime, long timeoutMillis) {
    AppRunRecord appRunRecord = null;

    do {
      try {
        if (System.currentTimeMillis() - startTime > timeoutMillis) {
          return false;
        }
        appRunRecord = repository.getLatestAppRunsAfterStartTime(app, startTime);
      } catch (Exception ignore) {
      }
    } while (!isRunCompleted(appRunRecord));

    return appRunRecord.getStatus().equals(AppRunRecord.Status.SUCCESS)
        || appRunRecord.getStatus().equals(AppRunRecord.Status.COMPLETED);
  }

  private boolean isRunCompleted(AppRunRecord appRunRecord) {
    if (appRunRecord == null) {
      return false;
    }
    return !nullOrEmpty(appRunRecord.getExecutionTime());
  }

  private IngestionPipeline deployIngestionPipeline(
      PipelineServiceClientInterface pipelineServiceClient, App app) {
    IngestionPipelineRepository repository =
        (IngestionPipelineRepository) Entity.getEntityRepository(Entity.INGESTION_PIPELINE);

    EntityReference pipelineRef = app.getPipelines().get(0);

    UMetadataApplicationConfig config = repository.getUMetadataApplicationConfig();

    IngestionPipeline ingestionPipeline = repository.get(null, pipelineRef.getId(), EMPTY_FIELDS);
    ingestionPipeline.setuMetadataServerConnection(
        new UMetadataConnectionBuilder(config).build());

    Map<String, Object> ingestionPipelineConfig =
        JsonUtils.readOrConvertValue(ingestionPipeline.getSourceConfig().getConfig(), Map.class);
    ingestionPipelineConfig.put("appConfig", app.getAppConfiguration());
    ingestionPipeline.getSourceConfig().setConfig(ingestionPipelineConfig);

    pipelineServiceClient.deployPipeline(
        ingestionPipeline,
        Entity.getEntity(ingestionPipeline.getService(), "ingestionRunner", Include.NON_DELETED));

    return ingestionPipeline;
  }

  private boolean runIngestionPipeline(
      PipelineServiceClientInterface pipelineServiceClient,
      IngestionPipeline ingestionPipeline,
      boolean waitForCompletion,
      long startTime,
      long timeoutMillis) {
    IngestionPipelineRepository repository =
        (IngestionPipelineRepository) Entity.getEntityRepository(Entity.INGESTION_PIPELINE);

    pipelineServiceClient.runPipeline(
        ingestionPipeline,
        Entity.getEntity(ingestionPipeline.getService(), "ingestionRunner", Include.NON_DELETED));

    if (waitForCompletion) {
      return waitForCompletion(repository, ingestionPipeline, startTime, timeoutMillis);
    } else {
      return true;
    }
  }

  // External App Logic
  private boolean runApp(
      PipelineServiceClientInterface pipelineServiceClient,
      App app,
      boolean waitForCompletion,
      long startTime,
      long timeoutMillis) {
    IngestionPipeline ingestionPipeline = deployIngestionPipeline(pipelineServiceClient, app);
    return runIngestionPipeline(
        pipelineServiceClient, ingestionPipeline, waitForCompletion, startTime, timeoutMillis);
  }

  private boolean waitForCompletion(
      IngestionPipelineRepository repository,
      IngestionPipeline ingestionPipeline,
      long startTime,
      long timeoutMillis) {
    while (true) {
      if (System.currentTimeMillis() - startTime > timeoutMillis) {
        return false;
      }

      List<PipelineStatus> statuses =
          repository
              .listPipelineStatus(
                  ingestionPipeline.getFullyQualifiedName(), startTime, startTime + timeoutMillis)
              .getData();

      if (statuses.isEmpty()) {
        continue;
      }

      PipelineStatus status = statuses.get(statuses.size() - 1);

      if (status.getPipelineState().equals(PipelineStatusType.FAILED)) {
        return false;
      } else if (status.getPipelineState().equals(PipelineStatusType.SUCCESS)
          || status.getPipelineState().equals(PipelineStatusType.PARTIAL_SUCCESS)) {
        return true;
      }
    }
  }
}
