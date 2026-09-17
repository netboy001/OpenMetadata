#  Copyright 2025 Collate
#  Licensed under the Collate Community License, Version 1.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#  https://wondersgroup.com/u-metadata/UMetadata/blob/main/ingestion/LICENSE
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

"""
UMetadata high-level API App test
"""
from unittest import TestCase

from _umetadata_testutils.umeta import int_admin_umeta
from metadata.generated.schema.entity.applications.app import App


class UMetaTableTest(TestCase):
    """
    Run this integration test with the local API available
    Install the ingestion package before running the tests
    """

    service_entity_id = None

    metadata = int_admin_umeta()

    def test_get_app(self):
        """We can GET an app via the client"""
        app = self.metadata.get_by_name(entity=App, fqn="SearchIndexingApplication")
        self.assertIsNotNone(app)
        self.assertEqual(app.name.root, "SearchIndexingApplication")
