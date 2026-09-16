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
We can load UMetadata from the environment variables
"""

import pytest

from _umetadata_testutils.umeta import OM_JWT
from metadata.ingestion.umeta.umeta_api import UMetadata


@pytest.mark.parametrize(
    "env_vars",
    [
        [
            ("UMETADATA__connection__hostPort", "http://localhost:8585/api"),
            ("UMETADATA__connection__authProvider", "umetadata"),
            ("UMETADATA__connection__securityConfig__jwtToken", OM_JWT),
        ],
        [
            ("UMETADATA__CONNECTION__HOSTPORT", "http://localhost:8585/api"),
            ("UMETADATA__CONNECTION__AUTHPROVIDER", "umetadata"),
            ("UMETADATA__CONNECTION__SECURITYCONFIG__JWTTOKEN", OM_JWT),
        ],
    ],
)
def test_umeta_from_env(monkeypatch, env_vars):
    # Set environment variables
    for var, value in env_vars:
        monkeypatch.setenv(var, value)

    umeta = UMetadata.from_env()
    assert umeta.health_check()
