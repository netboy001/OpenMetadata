package org.umetadata.service.search;

import lombok.extern.slf4j.Slf4j;
import org.umetadata.schema.analytics.ReportData;
import org.umetadata.schema.entity.ai.AIApplication;
import org.umetadata.schema.entity.ai.AIGovernancePolicy;
import org.umetadata.schema.entity.ai.LLMModel;
import org.umetadata.schema.entity.ai.PromptTemplate;
import org.umetadata.schema.entity.classification.Classification;
import org.umetadata.schema.entity.classification.Tag;
import org.umetadata.schema.entity.data.APICollection;
import org.umetadata.schema.entity.data.APIEndpoint;
import org.umetadata.schema.entity.data.Chart;
import org.umetadata.schema.entity.data.Container;
import org.umetadata.schema.entity.data.Dashboard;
import org.umetadata.schema.entity.data.DashboardDataModel;
import org.umetadata.schema.entity.data.Database;
import org.umetadata.schema.entity.data.DatabaseSchema;
import org.umetadata.schema.entity.data.Directory;
import org.umetadata.schema.entity.data.File;
import org.umetadata.schema.entity.data.Glossary;
import org.umetadata.schema.entity.data.GlossaryTerm;
import org.umetadata.schema.entity.data.Metric;
import org.umetadata.schema.entity.data.MlModel;
import org.umetadata.schema.entity.data.Pipeline;
import org.umetadata.schema.entity.data.Query;
import org.umetadata.schema.entity.data.QueryCostRecord;
import org.umetadata.schema.entity.data.Spreadsheet;
import org.umetadata.schema.entity.data.StoredProcedure;
import org.umetadata.schema.entity.data.Table;
import org.umetadata.schema.entity.data.Topic;
import org.umetadata.schema.entity.data.Worksheet;
import org.umetadata.schema.entity.domains.DataProduct;
import org.umetadata.schema.entity.domains.Domain;
import org.umetadata.schema.entity.services.*;
import org.umetadata.schema.entity.services.ApiService;
import org.umetadata.schema.entity.services.LLMService;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.entity.teams.Team;
import org.umetadata.schema.entity.teams.User;
import org.umetadata.schema.tests.TestCase;
import org.umetadata.schema.tests.TestSuite;
import org.umetadata.schema.tests.type.TestCaseResolutionStatus;
import org.umetadata.schema.tests.type.TestCaseResult;
import org.umetadata.service.Entity;
import org.umetadata.service.search.indexes.APICollectionIndex;
import org.umetadata.service.search.indexes.APIEndpointIndex;
import org.umetadata.service.search.indexes.APIServiceIndex;
import org.umetadata.service.search.indexes.AggregatedCostAnalysisReportDataIndex;
import org.umetadata.service.search.indexes.AiApplicationIndex;
import org.umetadata.service.search.indexes.AiGovernancePolicyIndex;
import org.umetadata.service.search.indexes.ChartIndex;
import org.umetadata.service.search.indexes.ClassificationIndex;
import org.umetadata.service.search.indexes.ContainerIndex;
import org.umetadata.service.search.indexes.DashboardDataModelIndex;
import org.umetadata.service.search.indexes.DashboardIndex;
import org.umetadata.service.search.indexes.DashboardServiceIndex;
import org.umetadata.service.search.indexes.DataProductIndex;
import org.umetadata.service.search.indexes.DatabaseIndex;
import org.umetadata.service.search.indexes.DatabaseSchemaIndex;
import org.umetadata.service.search.indexes.DatabaseServiceIndex;
import org.umetadata.service.search.indexes.DirectoryIndex;
import org.umetadata.service.search.indexes.DomainIndex;
import org.umetadata.service.search.indexes.DriveServiceIndex;
import org.umetadata.service.search.indexes.EntityReportDataIndex;
import org.umetadata.service.search.indexes.FileIndex;
import org.umetadata.service.search.indexes.GlossaryIndex;
import org.umetadata.service.search.indexes.GlossaryTermIndex;
import org.umetadata.service.search.indexes.IngestionPipelineIndex;
import org.umetadata.service.search.indexes.LlmModelIndex;
import org.umetadata.service.search.indexes.LlmServiceIndex;
import org.umetadata.service.search.indexes.MessagingServiceIndex;
import org.umetadata.service.search.indexes.MetadataServiceIndex;
import org.umetadata.service.search.indexes.MetricIndex;
import org.umetadata.service.search.indexes.MlModelIndex;
import org.umetadata.service.search.indexes.MlModelServiceIndex;
import org.umetadata.service.search.indexes.PipelineExecutionIndex;
import org.umetadata.service.search.indexes.PipelineIndex;
import org.umetadata.service.search.indexes.PipelineServiceIndex;
import org.umetadata.service.search.indexes.PromptTemplateIndex;
import org.umetadata.service.search.indexes.QueryCostRecordIndex;
import org.umetadata.service.search.indexes.QueryIndex;
import org.umetadata.service.search.indexes.RawCostAnalysisReportDataIndex;
import org.umetadata.service.search.indexes.SearchEntityIndex;
import org.umetadata.service.search.indexes.SearchIndex;
import org.umetadata.service.search.indexes.SearchServiceIndex;
import org.umetadata.service.search.indexes.SecurityServiceIndex;
import org.umetadata.service.search.indexes.SpreadsheetIndex;
import org.umetadata.service.search.indexes.StorageServiceIndex;
import org.umetadata.service.search.indexes.StoredProcedureIndex;
import org.umetadata.service.search.indexes.TableIndex;
import org.umetadata.service.search.indexes.TagIndex;
import org.umetadata.service.search.indexes.TeamIndex;
import org.umetadata.service.search.indexes.TestCaseIndex;
import org.umetadata.service.search.indexes.TestCaseResolutionStatusIndex;
import org.umetadata.service.search.indexes.TestCaseResultIndex;
import org.umetadata.service.search.indexes.TestSuiteIndex;
import org.umetadata.service.search.indexes.TopicIndex;
import org.umetadata.service.search.indexes.UserIndex;
import org.umetadata.service.search.indexes.WebAnalyticEntityViewReportDataIndex;
import org.umetadata.service.search.indexes.WebAnalyticUserActivityReportDataIndex;
import org.umetadata.service.search.indexes.WorksheetIndex;

@Slf4j
public class SearchIndexFactory {

  /**
   * Returns the minimal set of fields the reindex path must request from
   * {@code EntityRepository.setFields} for the given entity type. Probes the corresponding
   * index class via {@link #buildIndex(String, Object)} with a {@code null} entity and calls
   * {@link SearchIndex#getRequiredReindexFields()}. Index constructors must be safe with a null
   * entity for this probe to work — they are today because field declarations are static.
   */
  public java.util.Set<String> getReindexFieldsFor(String entityType) {
    try {
      SearchIndex probe = buildIndex(entityType, null);
      if (probe != null) {
        return probe.getRequiredReindexFields();
      }
    } catch (Exception e) {
      LOG.warn(
          "Failed to probe reindex fields for entity type {}; falling back to common set: {}",
          entityType,
          e.getMessage());
    }
    return SearchIndex.COMMON_REINDEX_FIELDS;
  }

  public SearchIndex buildIndex(String entityType, Object entity) {
    return switch (entityType) {
      case Entity.TABLE -> new TableIndex((Table) entity);
      case Entity.DASHBOARD -> new DashboardIndex((Dashboard) entity);
      case Entity.TOPIC -> new TopicIndex((Topic) entity);
      case Entity.PIPELINE -> new PipelineIndex((Pipeline) entity);
      case Entity.INGESTION_PIPELINE -> new IngestionPipelineIndex((IngestionPipeline) entity);
      case Entity.USER -> new UserIndex((User) entity);
      case Entity.TEAM -> new TeamIndex((Team) entity);
      case Entity.METRIC -> new MetricIndex((Metric) entity);
      case Entity.GLOSSARY -> new GlossaryIndex((Glossary) entity);
      case Entity.GLOSSARY_TERM -> new GlossaryTermIndex((GlossaryTerm) entity);
      case Entity.MLMODEL -> new MlModelIndex((MlModel) entity);
      case Entity.LLM_MODEL -> new LlmModelIndex((LLMModel) entity);
      case Entity.AI_APPLICATION -> new AiApplicationIndex((AIApplication) entity);
      case Entity.PROMPT_TEMPLATE -> new PromptTemplateIndex((PromptTemplate) entity);
      case Entity.AI_GOVERNANCE_POLICY -> new AiGovernancePolicyIndex((AIGovernancePolicy) entity);
      case Entity.TAG -> new TagIndex((Tag) entity);
      case Entity.CLASSIFICATION -> new ClassificationIndex((Classification) entity);
      case Entity.QUERY -> new QueryIndex((Query) entity);
      case Entity.QUERY_COST_RECORD -> new QueryCostRecordIndex((QueryCostRecord) entity);
      case Entity.CONTAINER -> new ContainerIndex((Container) entity);
      case Entity.DATABASE -> new DatabaseIndex((Database) entity);
      case Entity.DATABASE_SCHEMA -> new DatabaseSchemaIndex((DatabaseSchema) entity);
      case Entity.TEST_CASE -> new TestCaseIndex((TestCase) entity);
      case Entity.TEST_SUITE -> new TestSuiteIndex((TestSuite) entity);
      case Entity.CHART -> new ChartIndex((Chart) entity);
      case Entity.DASHBOARD_DATA_MODEL -> new DashboardDataModelIndex((DashboardDataModel) entity);
      case Entity.API_COLLECTION -> new APICollectionIndex((APICollection) entity);
      case Entity.API_ENDPOINT -> new APIEndpointIndex((APIEndpoint) entity);
      case Entity.DASHBOARD_SERVICE -> new DashboardServiceIndex((DashboardService) entity);
      case Entity.DATABASE_SERVICE -> new DatabaseServiceIndex((DatabaseService) entity);
      case Entity.MESSAGING_SERVICE -> new MessagingServiceIndex((MessagingService) entity);
      case Entity.MLMODEL_SERVICE -> new MlModelServiceIndex((MlModelService) entity);
      case Entity.LLM_SERVICE -> new LlmServiceIndex((LLMService) entity);
      case Entity.SEARCH_SERVICE -> new SearchServiceIndex((SearchService) entity);
      case Entity.SECURITY_SERVICE -> new SecurityServiceIndex((SecurityService) entity);
      case Entity.API_SERVICE -> new APIServiceIndex((ApiService) entity);
      case Entity.SEARCH_INDEX -> new SearchEntityIndex(
          (org.umetadata.schema.entity.data.SearchIndex) entity);
      case Entity.PIPELINE_SERVICE -> new PipelineServiceIndex((PipelineService) entity);
      case Entity.STORAGE_SERVICE -> new StorageServiceIndex((StorageService) entity);
      case Entity.DRIVE_SERVICE -> new DriveServiceIndex((DriveService) entity);
      case Entity.DOMAIN -> new DomainIndex((Domain) entity);
      case Entity.STORED_PROCEDURE -> new StoredProcedureIndex((StoredProcedure) entity);
      case Entity.DIRECTORY -> new DirectoryIndex((Directory) entity);
      case Entity.FILE -> new FileIndex((File) entity);
      case Entity.SPREADSHEET -> new SpreadsheetIndex((Spreadsheet) entity);
      case Entity.WORKSHEET -> new WorksheetIndex((Worksheet) entity);
      case Entity.DATA_PRODUCT -> new DataProductIndex((DataProduct) entity);
      case Entity.METADATA_SERVICE -> new MetadataServiceIndex((MetadataService) entity);
      case Entity.ENTITY_REPORT_DATA -> new EntityReportDataIndex((ReportData) entity);
      case Entity.WEB_ANALYTIC_ENTITY_VIEW_REPORT_DATA -> new WebAnalyticEntityViewReportDataIndex(
          (ReportData) entity);
      case Entity
          .WEB_ANALYTIC_USER_ACTIVITY_REPORT_DATA -> new WebAnalyticUserActivityReportDataIndex(
          (ReportData) entity);
      case Entity.RAW_COST_ANALYSIS_REPORT_DATA -> new RawCostAnalysisReportDataIndex(
          (ReportData) entity);
      case Entity.AGGREGATED_COST_ANALYSIS_REPORT_DATA -> new AggregatedCostAnalysisReportDataIndex(
          (ReportData) entity);
      case Entity.TEST_CASE_RESOLUTION_STATUS -> new TestCaseResolutionStatusIndex(
          (TestCaseResolutionStatus) entity);
      case Entity.TEST_CASE_RESULT -> new TestCaseResultIndex((TestCaseResult) entity);
      case Entity.PIPELINE_EXECUTION -> {
        PipelineExecutionIndex.PipelineExecutionData data =
            (PipelineExecutionIndex.PipelineExecutionData) entity;
        yield data == null
            ? new PipelineExecutionIndex(null, null)
            : new PipelineExecutionIndex(data.getPipeline(), data.getPipelineStatus());
      }
      default -> buildExternalIndexes(entityType, entity);
    };
  }

  protected SearchIndex buildExternalIndexes(String entityType, Object entity) {
    throw new IllegalArgumentException(
        String.format(
            "Entity Type [%s] is not valid for Index Factory, Entity: %s", entityType, entity));
  }
}
