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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.umetadata.service.util.TestUtils.ADMIN_AUTH_HEADERS;

import jakarta.ws.rs.client.WebTarget;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.umetadata.schema.api.ai.CreateAIApplication;
import org.umetadata.schema.entity.ai.AIApplication;
import org.umetadata.schema.entity.ai.AgentExecution;
import org.umetadata.schema.entity.ai.ExecutionStatus;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.utils.ResultList;
import org.umetadata.service.UMetadataApplicationTest;
import org.umetadata.service.resources.ai.AgentExecutionResource.AgentExecutionList;
import org.umetadata.service.util.TestUtils;

@Slf4j
class AgentExecutionResourceTest extends UMetadataApplicationTest {

  private static final String collectionName = "agentExecutions";
  private static AIApplication testAgent;
  private static EntityReference testAgentRef;

  @BeforeAll
  public void setup(TestInfo test) throws Exception {
    // We need to create dependencies for AgentExecution
    // Create LLMService, LLMModel, and AIApplication using REST API directly

    // Create LLMService
    org.umetadata.schema.api.services.CreateLLMService createService =
        new org.umetadata.schema.api.services.CreateLLMService()
            .withName("test-llm-service-agent-exec")
            .withServiceType(
                org.umetadata.schema.api.services.CreateLLMService.LlmServiceType.OpenAI)
            .withConnection(
                new org.umetadata.schema.type.LLMConnection()
                    .withConfig(
                        new org.umetadata.schema.services.connections.llm.OpenAIConnection()
                            .withApiKey("test-key")
                            .withBaseURL("https://api.openai.com/v1")));
    org.umetadata.schema.entity.services.LLMService llmService =
        TestUtils.post(
            getResource("services/llmServices"),
            createService,
            org.umetadata.schema.entity.services.LLMService.class,
            201,
            ADMIN_AUTH_HEADERS);

    // Create LLMModel
    org.umetadata.schema.api.ai.CreateLLMModel createModel =
        new org.umetadata.schema.api.ai.CreateLLMModel()
            .withName("test-model-agent-exec")
            .withBaseModel("gpt-4")
            .withService(llmService.getFullyQualifiedName());
    org.umetadata.schema.entity.ai.LLMModel llmModel =
        TestUtils.post(
            getResource("llmModels"),
            createModel,
            org.umetadata.schema.entity.ai.LLMModel.class,
            201,
            ADMIN_AUTH_HEADERS);

    // Create AIApplication
    org.umetadata.schema.entity.ai.ModelConfiguration modelConfig =
        new org.umetadata.schema.entity.ai.ModelConfiguration()
            .withModel(llmModel.getEntityReference())
            .withPurpose(org.umetadata.schema.entity.ai.ModelPurpose.Primary);
    CreateAIApplication createAgent =
        new CreateAIApplication()
            .withName("test-agent-for-execution")
            .withApplicationType(org.umetadata.schema.entity.ai.ApplicationType.Chatbot)
            .withModelConfigurations(new java.util.ArrayList<>(java.util.List.of(modelConfig)));
    testAgent =
        TestUtils.post(
            getResource("aiApplications"),
            createAgent,
            AIApplication.class,
            201,
            ADMIN_AUTH_HEADERS);
    testAgentRef = testAgent.getEntityReference();
  }

  @Test
  void post_agent_execution_200() throws IOException {
    assertNotNull(testAgent, "Test agent should have been created in setup");
    assertNotNull(testAgentRef, "Test agent reference should exist");

    AgentExecution execution = createAgentExecution();
    AgentExecution posted = postAgentExecution(execution, ADMIN_AUTH_HEADERS);
    assertNotNull(posted.getId());
    assertEquals(testAgentRef.getId(), posted.getAgentId());
    assertEquals(execution.getStatus(), posted.getStatus());
    assertEquals(execution.getInput(), posted.getInput());
    assertEquals(execution.getOutput(), posted.getOutput());
  }

  @Test
  void get_agent_executions_by_agent_200() throws IOException {
    AgentExecution execution1 = createAgentExecution();
    AgentExecution execution2 = createAgentExecution();

    postAgentExecution(execution1, ADMIN_AUTH_HEADERS);
    postAgentExecution(execution2, ADMIN_AUTH_HEADERS);

    ResultList<AgentExecution> executions =
        getAgentExecutions(testAgentRef.getId(), null, null, ADMIN_AUTH_HEADERS);

    assertNotNull(executions);
  }

  @Test
  void delete_agent_execution_by_timestamp_200() throws IOException {
    Long timestamp = System.currentTimeMillis();
    AgentExecution execution = createAgentExecution().withTimestamp(timestamp);

    postAgentExecution(execution, ADMIN_AUTH_HEADERS);

    deleteAgentExecutionData(testAgentRef.getId(), timestamp, ADMIN_AUTH_HEADERS);

    ResultList<AgentExecution> executions =
        getAgentExecutions(testAgentRef.getId(), timestamp, timestamp, ADMIN_AUTH_HEADERS);

    assertEquals(0, executions.getData().size());
  }

  private AgentExecution createAgentExecution() {
    return new AgentExecution()
        .withAgent(testAgentRef)
        .withAgentId(testAgentRef.getId())
        .withTimestamp(System.currentTimeMillis())
        .withStatus(ExecutionStatus.Success)
        .withInput("Test input")
        .withOutput("Test output");
  }

  private AgentExecution postAgentExecution(
      AgentExecution agentExecution, Map<String, String> authHeaders) throws HttpResponseException {
    WebTarget target = getResource(collectionName);
    return TestUtils.post(target, agentExecution, AgentExecution.class, 200, authHeaders);
  }

  private ResultList<AgentExecution> getAgentExecutions(
      UUID agentId, Long startTs, Long endTs, Map<String, String> authHeaders)
      throws HttpResponseException {
    WebTarget target = getResource(collectionName);
    target = target.queryParam("agentId", agentId);
    if (startTs != null) {
      target = target.queryParam("startTs", startTs);
    }
    if (endTs != null) {
      target = target.queryParam("endTs", endTs);
    }
    return TestUtils.get(target, AgentExecutionList.class, authHeaders);
  }

  private void deleteAgentExecutionData(
      UUID agentId, Long timestamp, Map<String, String> authHeaders) throws IOException {
    String url = String.format("/%s/%s", agentId, timestamp);
    WebTarget target = getResource(collectionName).path(url);
    TestUtils.delete(target, authHeaders);
  }
}
