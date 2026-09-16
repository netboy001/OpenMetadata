package org.umetadata.service.apps;

import io.dropwizard.core.setup.Environment;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.security.Authorizer;

/**
 * Interface for MCP Server Provider to avoid circular dependency.
 * The actual implementation will be in umetadata-mcp module.
 */
public interface McpServerProvider {
  /**
   * Initialize and register the MCP server with the application.
   */
  void initializeMcpServer(
      Environment environment,
      Authorizer authorizer,
      Limits limits,
      UMetadataApplicationConfig config);
}
