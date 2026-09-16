package org.umetadata.it.factories;

import java.util.UUID;
import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.schema.api.services.CreateLLMService;
import org.umetadata.schema.api.services.CreateLLMService.LlmServiceType;
import org.umetadata.schema.entity.services.LLMService;
import org.umetadata.schema.services.connections.llm.OpenAIConnection;
import org.umetadata.schema.type.LLMConnection;

public class LLMServiceTestFactory {

  public static LLMService createOpenAI(TestNamespace ns) {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    String name = ns.prefix("llmService_" + uniqueId);

    OpenAIConnection openAIConn =
        new OpenAIConnection().withApiKey("test-key").withBaseURL("https://api.openai.com/v1");

    LLMConnection conn = new LLMConnection().withConfig(openAIConn);

    CreateLLMService request =
        new CreateLLMService()
            .withName(name)
            .withServiceType(LlmServiceType.OpenAI)
            .withConnection(conn)
            .withDescription("Test OpenAI service");

    return SdkClients.adminClient().llmServices().create(request);
  }

  public static LLMService getById(String id) {
    return SdkClients.adminClient().llmServices().get(id);
  }
}
