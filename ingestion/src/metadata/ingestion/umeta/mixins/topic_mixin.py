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
Mixin class containing Topic specific methods

To be used by UMetadata class
"""

from metadata.generated.schema.entity.data.topic import Topic, TopicSampleData
from metadata.ingestion.umeta.client import REST
from metadata.utils.logger import umeta_logger

logger = umeta_logger()


class UMetaTopicMixin:
    """
    UMetadata API methods related to Topics.

    To be inherited by UMetadata
    """

    client: REST

    def ingest_topic_sample_data(
        self, topic: Topic, sample_data: TopicSampleData
    ) -> TopicSampleData:
        """
        PUT sample data for a topic

        :param topic: Topic Entity to update
        :param sample_data: Data to add
        """
        resp = self.client.put(
            f"{self.get_suffix(Topic)}/{topic.id.root}/sampleData",
            data=sample_data.model_dump_json(),
        )
        return TopicSampleData(**resp["sampleData"])
