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

package org.umetadata.service.clients.pipeline.config.types;

import static org.umetadata.service.clients.pipeline.config.WorkflowConfigBuilder.buildDefaultSink;
import static org.umetadata.service.clients.pipeline.config.WorkflowConfigBuilder.buildDefaultSource;

import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.entity.services.ingestionPipelines.IngestionPipeline;
import org.umetadata.schema.metadataIngestion.UMetadataWorkflowConfig;
import org.umetadata.schema.metadataIngestion.Sink;
import org.umetadata.schema.metadataIngestion.Source;

public class MetadataWorkflowConfig implements WorkflowConfigTypeStrategy {

  public UMetadataWorkflowConfig buildOMWorkflowConfig(
      IngestionPipeline ingestionPipeline, ServiceEntityInterface service)
      throws WorkflowBuildException {
    UMetadataWorkflowConfig config = new UMetadataWorkflowConfig();

    Source source = buildDefaultSource(ingestionPipeline, service);
    Sink sink = buildDefaultSink();

    config.setSource(source);
    config.setSink(sink);

    return config;
  }
}
