# pyright: reportUnknownMemberType=false, reportUnknownVariableType=false
from typing import Any, List, Optional

from metadata.generated.schema.metadataIngestion.workflow import (
    UMetadataWorkflowConfig,
)
from metadata.ingestion.api.parser import parse_workflow_config_gracefully
from metadata.ingestion.umeta.umeta_api import UMetadata
from metadata.pii.base_processor import AutoClassificationProcessor
from metadata.pii.processor import PIIProcessor
from metadata.pii.tag_processor import TagProcessor


def create_pii_processor(
    metadata: UMetadata[Any, Any],
    umetadata_config: UMetadataWorkflowConfig,
    classification_filter: Optional[List[str]] = None,
) -> AutoClassificationProcessor:
    processor_type = getattr(umetadata_config.processor, "type", "tag-pii-processor")
    if processor_type == "tag-pii-processor":
        return TagProcessor(
            config=parse_workflow_config_gracefully(umetadata_config.model_dump()),
            metadata=metadata,
            classification_filter=classification_filter,
        )
    return PIIProcessor.create(
        umetadata_config.model_dump(),
        metadata,
    )
