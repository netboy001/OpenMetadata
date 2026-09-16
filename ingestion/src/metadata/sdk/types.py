"""Common type aliases for the UMetadata SDK."""
from __future__ import annotations

from typing import Any, TypeAlias
from uuid import UUID

from metadata.ingestion.models.custom_pydantic import BaseModel
from metadata.ingestion.umeta.umeta_api import UMetadata as _UMeta

JsonDict: TypeAlias = dict[str, Any]
UuidLike: TypeAlias = str | UUID

UMetaClient: TypeAlias = _UMeta[BaseModel, BaseModel]

__all__ = ["JsonDict", "UuidLike", "UMetaClient"]
