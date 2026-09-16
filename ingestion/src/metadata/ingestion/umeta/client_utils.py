#  Copyright 2025 Collate
#  Licensed under the Collate Community License, Version 1.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#  https://github.com/u-metadata/UMetadata/blob/main/ingestion/LICENSE
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
"""
UMeta client create helpers
"""
import traceback
from typing import List, Optional  # noqa: UP035

from metadata.generated.schema.entity.data.chart import Chart
from metadata.generated.schema.entity.services.connections.metadata.uMetadataConnection import (
    UMetadataConnection,
)
from metadata.generated.schema.type.basic import FullyQualifiedEntityName
from metadata.ingestion.umeta.umeta_api import C, UMetadata, T
from metadata.utils import fqn
from metadata.utils.logger import umeta_logger

logger = umeta_logger()


def create_umeta_client(
    metadata_config: UMetadataConnection,
    user_agent: Optional[str] = None,  # noqa: UP045
) -> UMetadata[T, C]:  # pyright: ignore[reportInvalidTypeVarUse]
    """Create an UMetadata client

    Args:
        metadata_config (UMetadataConnection): OM connection config
        user_agent (Optional[str]): Value for the HTTP User-Agent header, identifying
            the workflow issuing the requests (e.g. ``snowflake_metadata``)

    Returns:
        UMetadata: an OM client
    """
    try:
        metadata = UMetadata[T, C](
            metadata_config,
            additional_client_config_arguments=(
                {"user_agent": user_agent} if user_agent else None
            ),
        )
        metadata.health_check()
        return metadata
    except Exception as exc:
        logger.debug(traceback.format_exc())
        logger.warning(f"Wild error initialising the UMeta Client {exc}")
        raise ValueError(exc)


def get_chart_entities_from_id(
    chart_ids: List[str], metadata: UMetadata, service_name: str
) -> List[FullyQualifiedEntityName]:
    """
    Method to get the chart entity using get_by_name api
    """

    entities = []
    for chart_id in chart_ids:
        chart: Chart = metadata.get_by_name(
            entity=Chart,
            fqn=fqn.build(
                metadata, Chart, chart_name=str(chart_id), service_name=service_name
            ),
        )
        if chart:
            entities.append(chart.fullyQualifiedName)
    return entities
