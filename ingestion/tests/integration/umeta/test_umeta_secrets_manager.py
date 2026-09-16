#  Copyright 2022 Collate
#  Licensed under the Collate Community License, Version 1.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#  https://github.com/u-metadata/UMetadata/blob/main/ingestion/LICENSE
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
import os
from unittest import TestCase, mock

from metadata.generated.schema.entity.services.connections.metadata.uMetadataConnection import (
    UMetadataConnection,
)
from metadata.generated.schema.security.secrets.secretsManagerClientLoader import (
    SecretsManagerClientLoader,
)
from metadata.generated.schema.security.secrets.secretsManagerProvider import (
    SecretsManagerProvider,
)
from metadata.ingestion.umeta.auth_provider import UMetadataAuthenticationProvider
from metadata.ingestion.umeta.umeta_api import UMetadata
from metadata.utils.secrets.aws_secrets_manager import AWSSecretsManager
from metadata.utils.secrets.db_secrets_manager import DBSecretsManager
from metadata.utils.singleton import Singleton


class UMetaSecretManagerTest(TestCase):
    metadata: UMetadata
    aws_server_config: UMetadataConnection
    local_server_config: UMetadataConnection

    @classmethod
    def setUp(cls) -> None:
        Singleton.clear_all()
        cls.local_server_config = UMetadataConnection(
            hostPort="http://localhost:8585/api",
            enableVersionValidation=False,
        )
        cls.aws_server_config = UMetadataConnection(
            hostPort="http://localhost:8585/api",
            secretsManagerProvider=SecretsManagerProvider.aws,
            secretsManagerLoader=SecretsManagerClientLoader.noop,
            enableVersionValidation=False,
        )

    def test_umeta_with_local_secret_manager(self):
        self._init_local_secret_manager()
        assert type(self.metadata.secrets_manager_client) is DBSecretsManager
        assert type(self.metadata._auth_provider) is UMetadataAuthenticationProvider

    @mock.patch.dict(os.environ, {"AWS_DEFAULT_REGION": "us-east-2"}, clear=True)
    def test_umeta_with_aws_secret_manager(self):
        self._init_aws_secret_manager()
        assert type(self.metadata.secrets_manager_client) is AWSSecretsManager
        assert type(self.metadata._auth_provider) is UMetadataAuthenticationProvider

    def _init_local_secret_manager(self):
        self.metadata = UMetadata(self.local_server_config)

    def _init_aws_secret_manager(self):
        self.metadata = UMetadata(self.aws_server_config)
