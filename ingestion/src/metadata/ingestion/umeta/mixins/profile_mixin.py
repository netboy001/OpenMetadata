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
Mixin class containing Pipeline specific methods

To be used by UMetadata class
"""

from typing import Optional, Type, TypeVar

from pydantic import BaseModel

from metadata.generated.schema.type.entityProfile import EntityProfile, ProfileTypeEnum
from metadata.ingestion.umeta.client import REST
from metadata.ingestion.umeta.models import EntityList
from metadata.ingestion.umeta.utils import get_entity_type
from metadata.utils.logger import umeta_logger

logger = umeta_logger()

T = TypeVar("T", bound=BaseModel)


class UMetaProfileMixin:
    """
    UMetadata API methods related to Profiles.

    To be inherited by UMetadata
    """

    client: REST

    def get_profile_data_by_type(
        self,
        entity_type: Type[T],
        start_ts: int,
        end_ts: int,
        profile_type: Optional[ProfileTypeEnum] = None,
    ) -> EntityList[EntityProfile]:
        """List all profile data for a given entity type. To get all the profile for
        a specific profile type use the profile_type parameter.

        Args:
            entity_type (Type[T]): Entity type
            start_ts (int): Start timestamp
            end_ts (int): End timestamp
            profile_type (Optional[ProfileTypeEnum]): Profile type

        Returns:
            EntityList[EntityProfile]: EntityList list object
        """
        entity_type = get_entity_type(entity_type)
        params = {
            "startTs": start_ts,
            "endTs": end_ts,
        }
        if profile_type:
            params["profileType"] = profile_type.value
        resp = self.client.get(
            f"{self.get_suffix(EntityProfile)}/{entity_type}",
            data=params,
        )
        paging = resp["paging"]
        return EntityList[EntityProfile](
            entities=[EntityProfile(**entity) for entity in resp["data"]],
            total=paging["total"],
            after=paging["after"],
            before=paging["before"],
        )
