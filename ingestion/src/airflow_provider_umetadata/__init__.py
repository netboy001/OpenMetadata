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
Airflow backend lineage module
"""


def get_provider_config():
    """
    Get provider configuration

    Returns
        dict:
    """
    return {
        "name": "UMetadata",
        "description": "`UMetadata <https://u-metadata.org/>`__",
        "package-name": "umetadata-ingestion",
        "version": "0.4.1",
        "connection-types": [
            {
                "connection-type": "umetadata",
                "hook-class-name": "airflow_provider_umetadata.hooks.umetadata.UMetadataHook",
            }
        ],
        "hook-class-names": [
            "airflow_provider_umetadata.hooks.umetadata.UMetadataHook",
        ],
    }
