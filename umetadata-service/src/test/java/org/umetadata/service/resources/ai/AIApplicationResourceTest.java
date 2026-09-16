/*
 *  Copyright 2021 Collate
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.umetadata.service.resources.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.umetadata.service.util.EntityUtil.fieldUpdated;
import static org.umetadata.service.util.TestUtils.ADMIN_AUTH_HEADERS;
import static org.umetadata.service.util.TestUtils.UpdateType.MINOR_UPDATE;
import static org.umetadata.service.util.TestUtils.UpdateType.NO_CHANGE;
import static org.umetadata.service.util.TestUtils.assertListNull;

import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.umetadata.schema.api.ai.CreateAIApplication;
import org.umetadata.schema.api.ai.CreateLLMModel;
import org.umetadata.schema.api.services.CreateLLMService;
import org.umetadata.schema.api.services.CreateLLMService.LlmServiceType;
import org.umetadata.schema.entity.ai.AIApplication;
import org.umetadata.schema.entity.ai.ApplicationType;
import org.umetadata.schema.entity.ai.LLMModel;
import org.umetadata.schema.entity.ai.ModelConfiguration;
import org.umetadata.schema.entity.ai.ModelPurpose;
import org.umetadata.schema.entity.services.LLMService;
import org.umetadata.schema.services.connections.llm.OpenAIConnection;
import org.umetadata.schema.type.ChangeDescription;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.LLMConnection;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.EntityResourceTest;
import org.umetadata.service.resources.ai.AIApplicationResource.AIApplicationList;
import org.umetadata.service.resources.services.llm.LLMServiceResourceTest;
import org.umetadata.service.util.TestUtils;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AIApplicationResourceTest
    extends EntityResourceTest<AIApplication, CreateAIApplication> {
  private static EntityReference TEST_LLM_MODEL_REF;

  public AIApplicationResourceTest() {
    super(
        Entity.AI_APPLICATION,
        AIApplication.class,
        AIApplicationList.class,
        "aiApplications",
        AIApplicationResource.FIELDS);
  }

  @BeforeAll
  public void setup(TestInfo test) throws IOException, java.net.URISyntaxException {
    super.setup(test);
    setupLLMModel(test);
  }

  private void setupLLMModel(TestInfo test) throws HttpResponseException {
    // Create LLMService
    LLMServiceResourceTest llmServiceResourceTest = new LLMServiceResourceTest();
    CreateLLMService createService =
        llmServiceResourceTest
            .createRequest(test)
            .withName("test-llm-service")
            .withServiceType(LlmServiceType.OpenAI)
            .withConnection(
                new LLMConnection()
                    .withConfig(
                        new OpenAIConnection()
                            .withApiKey("test-key")
                            .withBaseURL("https://api.openai.com/v1")));
    LLMService llmService = llmServiceResourceTest.createEntity(createService, ADMIN_AUTH_HEADERS);

    // Create LLMModel
    LLMModelResourceTest llmModelResourceTest = new LLMModelResourceTest();
    CreateLLMModel createModel =
        llmModelResourceTest
            .createRequest("test-model")
            .withService(llmService.getFullyQualifiedName());
    LLMModel llmModel = llmModelResourceTest.createEntity(createModel, ADMIN_AUTH_HEADERS);
    TEST_LLM_MODEL_REF = llmModel.getEntityReference();
  }

  @Test
  void post_validAIApplications_as_admin_200_OK(TestInfo test) throws IOException {
    CreateAIApplication create = createRequest(test);
    createAndCheckEntity(create, ADMIN_AUTH_HEADERS);

    create.withName(getEntityName(test, 1)).withDescription("description");
    createAndCheckEntity(create, ADMIN_AUTH_HEADERS);
  }

  @Test
  void put_AIApplicationUpdateWithNoChange_200(TestInfo test) throws IOException {
    CreateAIApplication request = createRequest(test).withOwners(List.of(USER1_REF));
    AIApplication application = createAndCheckEntity(request, ADMIN_AUTH_HEADERS);
    ChangeDescription change = getChangeDescription(application, NO_CHANGE);
    updateAndCheckEntity(request, Status.OK, ADMIN_AUTH_HEADERS, NO_CHANGE, change);
  }

  @Test
  void put_AIApplicationUpdateApplicationType_200(TestInfo test) throws IOException {
    CreateAIApplication request = createRequest(test).withApplicationType(ApplicationType.Chatbot);
    AIApplication application = createAndCheckEntity(request, ADMIN_AUTH_HEADERS);
    ChangeDescription change = getChangeDescription(application, MINOR_UPDATE);
    fieldUpdated(change, "applicationType", ApplicationType.Chatbot, ApplicationType.Agent);
    updateAndCheckEntity(
        request.withApplicationType(ApplicationType.Agent),
        Status.OK,
        ADMIN_AUTH_HEADERS,
        MINOR_UPDATE,
        change);
  }

  @Override
  public AIApplication validateGetWithDifferentFields(AIApplication application, boolean byName)
      throws HttpResponseException {
    String fields = "";
    application =
        byName
            ? getEntityByName(application.getFullyQualifiedName(), fields, ADMIN_AUTH_HEADERS)
            : getEntity(application.getId(), fields, ADMIN_AUTH_HEADERS);
    assertListNull(application.getOwners(), application.getFollowers(), application.getTags());

    fields = "owners,followers,tags";
    application =
        byName
            ? getEntityByName(application.getFullyQualifiedName(), fields, ADMIN_AUTH_HEADERS)
            : getEntity(application.getId(), fields, ADMIN_AUTH_HEADERS);
    return application;
  }

  @Override
  public CreateAIApplication createRequest(String name) {
    ModelConfiguration modelConfig =
        new ModelConfiguration().withModel(TEST_LLM_MODEL_REF).withPurpose(ModelPurpose.Primary);

    return new CreateAIApplication()
        .withName(name)
        .withApplicationType(ApplicationType.Chatbot)
        .withModelConfigurations(new ArrayList<>(List.of(modelConfig)));
  }

  @Override
  public void compareEntities(
      AIApplication expected, AIApplication updated, Map<String, String> authHeaders)
      throws HttpResponseException {
    assertEquals(expected.getApplicationType(), updated.getApplicationType());
    TestUtils.validateTags(expected.getTags(), updated.getTags());
    TestUtils.validateEntityReferences(updated.getFollowers());
  }

  @Override
  public void validateCreatedEntity(
      AIApplication createdEntity,
      CreateAIApplication createRequest,
      Map<String, String> authHeaders)
      throws HttpResponseException {
    assertEquals(createRequest.getApplicationType(), createdEntity.getApplicationType());
    TestUtils.validateTags(createRequest.getTags(), createdEntity.getTags());
    TestUtils.validateEntityReferences(createdEntity.getFollowers());
  }

  @Test
  void post_AIApplicationWithEUAIActCompliance_200(TestInfo test) throws IOException {
    CreateAIApplication request = createRequest(test);

    // Create EU AI Act compliance record
    org.umetadata.schema.type.EUAIActCompliance euCompliance =
        new org.umetadata.schema.type.EUAIActCompliance()
            .withRiskClassification(
                org.umetadata.schema.type.EUAIActCompliance.RiskClassification.HIGH)
            .withRiskRationale("Processes personal data for employment decisions");

    org.umetadata.schema.type.AIComplianceRecord complianceRecord =
        new org.umetadata.schema.type.AIComplianceRecord()
            .withFramework(org.umetadata.schema.type.ComplianceFramework.EU_AI_Act)
            .withStatus(org.umetadata.schema.type.AIComplianceRecord.Status.UNDER_REVIEW)
            .withAssessedBy("compliance-team")
            .withEuAIAct(euCompliance);

    org.umetadata.schema.type.AICompliance aiCompliance =
        new org.umetadata.schema.type.AICompliance()
            .withComplianceRecords(List.of(complianceRecord));

    org.umetadata.schema.entity.ai.GovernanceMetadata governance =
        new org.umetadata.schema.entity.ai.GovernanceMetadata()
            .withRegistrationStatus(
                org.umetadata.schema.entity.ai.GovernanceMetadata.RegistrationStatus.REGISTERED)
            .withAiCompliance(aiCompliance);

    request.withGovernanceMetadata(governance);
    AIApplication application = createAndCheckEntity(request, ADMIN_AUTH_HEADERS);

    // Verify compliance was saved
    assertEquals(
        org.umetadata.schema.entity.ai.GovernanceMetadata.RegistrationStatus.REGISTERED,
        application.getGovernanceMetadata().getRegistrationStatus());
    assertEquals(
        1, application.getGovernanceMetadata().getAiCompliance().getComplianceRecords().size());

    org.umetadata.schema.type.AIComplianceRecord savedRecord =
        application.getGovernanceMetadata().getAiCompliance().getComplianceRecords().get(0);
    assertEquals(
        org.umetadata.schema.type.ComplianceFramework.EU_AI_Act, savedRecord.getFramework());
    assertEquals(
        org.umetadata.schema.type.AIComplianceRecord.Status.UNDER_REVIEW,
        savedRecord.getStatus());
    assertEquals(
        org.umetadata.schema.type.EUAIActCompliance.RiskClassification.HIGH,
        savedRecord.getEuAIAct().getRiskClassification());
  }

  @Test
  void post_AIApplicationWithMultipleComplianceFrameworks_200(TestInfo test) throws IOException {
    CreateAIApplication request = createRequest(test);

    // EU AI Act compliance
    org.umetadata.schema.type.EUAIActCompliance euCompliance =
        new org.umetadata.schema.type.EUAIActCompliance()
            .withRiskClassification(
                org.umetadata.schema.type.EUAIActCompliance.RiskClassification.LIMITED);

    org.umetadata.schema.type.AIComplianceRecord euRecord =
        new org.umetadata.schema.type.AIComplianceRecord()
            .withFramework(org.umetadata.schema.type.ComplianceFramework.EU_AI_Act)
            .withStatus(org.umetadata.schema.type.AIComplianceRecord.Status.COMPLIANT)
            .withEuAIAct(euCompliance);

    // NIST AI RMF compliance
    org.umetadata.schema.type.EthicalAIAssessment ethicalAssessment =
        new org.umetadata.schema.type.EthicalAIAssessment()
            .withPrivacyLevel(
                org.umetadata.schema.type.EthicalAIAssessment.PrivacyLevel.SENSITIVE)
            .withFairnessRisk(org.umetadata.schema.type.EthicalAIAssessment.FairnessRisk.MEDIUM)
            .withBiasMitigationCoverage(
                org.umetadata.schema.type.EthicalAIAssessment.BiasMitigationCoverage.PARTIAL);

    org.umetadata.schema.type.AIComplianceRecord nistRecord =
        new org.umetadata.schema.type.AIComplianceRecord()
            .withFramework(org.umetadata.schema.type.ComplianceFramework.NIST_AI_RMF)
            .withStatus(org.umetadata.schema.type.AIComplianceRecord.Status.PARTIALLY_COMPLIANT)
            .withEthicalAssessment(ethicalAssessment);

    org.umetadata.schema.type.AICompliance aiCompliance =
        new org.umetadata.schema.type.AICompliance()
            .withComplianceRecords(List.of(euRecord, nistRecord));

    org.umetadata.schema.entity.ai.GovernanceMetadata governance =
        new org.umetadata.schema.entity.ai.GovernanceMetadata().withAiCompliance(aiCompliance);

    request.withGovernanceMetadata(governance);
    AIApplication application = createAndCheckEntity(request, ADMIN_AUTH_HEADERS);

    // Verify both compliance records
    assertEquals(
        2, application.getGovernanceMetadata().getAiCompliance().getComplianceRecords().size());
  }

  @Test
  void post_AIApplicationWithScopeAndDeployment_200(TestInfo test) throws IOException {
    CreateAIApplication request = createRequest(test);

    // Create scope and deployment info
    org.umetadata.schema.type.ScopeAndDeployment scopeDeployment =
        new org.umetadata.schema.type.ScopeAndDeployment()
            .withScope(org.umetadata.schema.type.ScopeAndDeployment.Scope.EXTERNAL)
            .withDeploymentRegions(List.of("EU", "US", "UK"))
            .withAffectedUserCount(10000);

    org.umetadata.schema.type.AIComplianceRecord complianceRecord =
        new org.umetadata.schema.type.AIComplianceRecord()
            .withFramework(org.umetadata.schema.type.ComplianceFramework.EU_AI_Act)
            .withStatus(org.umetadata.schema.type.AIComplianceRecord.Status.COMPLIANT)
            .withScopeAndDeployment(scopeDeployment);

    org.umetadata.schema.type.AICompliance aiCompliance =
        new org.umetadata.schema.type.AICompliance()
            .withComplianceRecords(List.of(complianceRecord));

    org.umetadata.schema.entity.ai.GovernanceMetadata governance =
        new org.umetadata.schema.entity.ai.GovernanceMetadata().withAiCompliance(aiCompliance);

    request.withGovernanceMetadata(governance);
    AIApplication application = createAndCheckEntity(request, ADMIN_AUTH_HEADERS);

    // Verify scope and deployment
    org.umetadata.schema.type.AIComplianceRecord savedRecord =
        application.getGovernanceMetadata().getAiCompliance().getComplianceRecords().get(0);
    assertEquals(
        org.umetadata.schema.type.ScopeAndDeployment.Scope.EXTERNAL,
        savedRecord.getScopeAndDeployment().getScope());
    assertEquals(3, savedRecord.getScopeAndDeployment().getDeploymentRegions().size());
    assertEquals(10000, savedRecord.getScopeAndDeployment().getAffectedUserCount());
  }

  @Test
  void put_AIApplicationUpdateCompliance_200(TestInfo test) throws IOException {
    // Create application with initial compliance
    CreateAIApplication request = createRequest(test);

    org.umetadata.schema.type.AIComplianceRecord complianceRecord =
        new org.umetadata.schema.type.AIComplianceRecord()
            .withFramework(org.umetadata.schema.type.ComplianceFramework.EU_AI_Act)
            .withStatus(org.umetadata.schema.type.AIComplianceRecord.Status.UNDER_REVIEW);

    org.umetadata.schema.type.AICompliance aiCompliance =
        new org.umetadata.schema.type.AICompliance()
            .withComplianceRecords(List.of(complianceRecord));

    org.umetadata.schema.entity.ai.GovernanceMetadata governance =
        new org.umetadata.schema.entity.ai.GovernanceMetadata().withAiCompliance(aiCompliance);

    request.withGovernanceMetadata(governance);
    AIApplication application = createAndCheckEntity(request, ADMIN_AUTH_HEADERS);

    // Verify initial status
    assertEquals(
        org.umetadata.schema.type.AIComplianceRecord.Status.UNDER_REVIEW,
        application
            .getGovernanceMetadata()
            .getAiCompliance()
            .getComplianceRecords()
            .get(0)
            .getStatus());

    // Update compliance status to Compliant
    org.umetadata.schema.type.AIComplianceRecord updatedRecord =
        new org.umetadata.schema.type.AIComplianceRecord()
            .withFramework(org.umetadata.schema.type.ComplianceFramework.EU_AI_Act)
            .withStatus(org.umetadata.schema.type.AIComplianceRecord.Status.COMPLIANT);

    org.umetadata.schema.type.AICompliance updatedCompliance =
        new org.umetadata.schema.type.AICompliance()
            .withComplianceRecords(List.of(updatedRecord));

    org.umetadata.schema.entity.ai.GovernanceMetadata updatedGovernance =
        new org.umetadata.schema.entity.ai.GovernanceMetadata()
            .withAiCompliance(updatedCompliance);

    request.withGovernanceMetadata(updatedGovernance);

    // Perform update
    AIApplication updatedApp = updateEntity(request, Status.OK, ADMIN_AUTH_HEADERS);

    // Verify status was updated
    assertEquals(
        org.umetadata.schema.type.AIComplianceRecord.Status.COMPLIANT,
        updatedApp
            .getGovernanceMetadata()
            .getAiCompliance()
            .getComplianceRecords()
            .get(0)
            .getStatus());
  }

  @Override
  public void assertFieldChange(String fieldName, Object expected, Object actual) {
    if (expected == actual) {
      return;
    }
    if (fieldName.equals("applicationType")) {
      ApplicationType expectedApplicationType = (ApplicationType) expected;
      ApplicationType actualApplicationType =
          actual instanceof String
              ? ApplicationType.fromValue((String) actual)
              : (ApplicationType) actual;
      assertEquals(expectedApplicationType, actualApplicationType);
    } else {
      assertCommonFieldChange(fieldName, expected, actual);
    }
  }
}
