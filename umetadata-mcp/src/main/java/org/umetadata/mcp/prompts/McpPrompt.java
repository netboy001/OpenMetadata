package org.umetadata.mcp.prompts;

import io.modelcontextprotocol.spec.McpSchema;

public interface McpPrompt {
  McpSchema.GetPromptResult callPrompt(McpSchema.GetPromptRequest promptRequest);
}
