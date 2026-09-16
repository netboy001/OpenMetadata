"""Extend the ProfilerSource class to add support for Databricks is_disconnect SQA method"""
from metadata.generated.schema.configuration.profilerConfiguration import (
    ProfilerConfiguration,
)
from metadata.generated.schema.entity.data.database import Database
from metadata.generated.schema.metadataIngestion.workflow import (
    UMetadataWorkflowConfig,
)
from metadata.ingestion.umeta.umeta_api import UMetadata
from metadata.profiler.source.database.base.profiler_source import ProfilerSource


# pylint: disable=unused-argument
def is_disconnect(self, e, connection, cursor):
    """is_disconnect method for the Databricks dialect"""
    if "Invalid SessionHandle: SessionHandle" in str(e):
        return True
    return False


class DataBricksProfilerSource(ProfilerSource):
    """Databricks Profiler source"""

    def __init__(
        self,
        config: UMetadataWorkflowConfig,
        database: Database,
        umeta_client: UMetadata,
        global_profiler_config: ProfilerConfiguration,
    ):
        super().__init__(config, database, umeta_client, global_profiler_config)
        self.set_is_disconnect()

    def set_is_disconnect(self):
        """Set the is_disconnect method for the Databricks dialect"""
        # pylint: disable=import-outside-toplevel
        from databricks.sqlalchemy import DatabricksDialect

        DatabricksDialect.is_disconnect = is_disconnect
