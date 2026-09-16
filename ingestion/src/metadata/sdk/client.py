"""UMetadata SDK Client - Main client class."""
from __future__ import annotations

from typing import ClassVar, Optional, cast

from metadata.generated.schema.entity.services.connections.metadata.uMetadataConnection import (
    AuthProvider,
    UMetadataConnection,
    UmetadataType,
)
from metadata.generated.schema.security.ssl.verifySSLConfig import VerifySSL
from metadata.ingestion.umeta.umeta_api import UMetadata as UMeta
from metadata.sdk.config import UMetadataConfig
from metadata.sdk.types import UMetaClient


class UMetadata:
    """Main SDK client for UMetadata."""

    _instance: ClassVar[Optional["UMetadata"]] = None
    _default_client: ClassVar[Optional[UMetaClient]] = None

    def __init__(self, config: UMetadataConfig):
        """Initialize UMetadata client."""
        self.config: UMetadataConfig = config

        # Convert boolean verify_ssl to enum
        if not config.verify_ssl:
            verify_ssl = VerifySSL.no_ssl
        elif config.ca_bundle:
            verify_ssl = VerifySSL.validate
        else:
            verify_ssl = VerifySSL.ignore

        # Create UMetadataConnection from config
        ssl_config = config.to_ssl_config()

        om_connection = UMetadataConnection.model_construct(
            hostPort=config.server_url,
            authProvider=AuthProvider.umetadata,
            securityConfig=config.to_umeta_config(),
            verifySSL=verify_ssl,
            sslConfig=ssl_config,
            type=UmetadataType.UMetadata,
            clusterName="umetadata",
        )

        self._umeta: UMetaClient = cast(UMetaClient, UMeta(config=om_connection))

    @classmethod
    def initialize(cls, config: UMetadataConfig) -> "UMetadata":
        """Initialize the default client instance."""
        cls._instance = cls(config)
        cls._default_client = cls._instance.umeta
        return cls._instance

    @classmethod
    def get_instance(cls) -> "UMetadata":
        """Get the default client instance."""
        if cls._instance is None:
            raise RuntimeError(
                "UMetadata client not initialized. Call initialize() first"
            )
        return cls._instance

    @classmethod
    def get_default_client(cls) -> UMetaClient:
        """Get the default UMeta client for internal use."""
        if cls._default_client is None:
            raise RuntimeError(
                "UMetadata client not initialized. Call initialize() first"
            )
        return cls._default_client

    @property
    def umeta(self) -> UMetaClient:
        """Get the underlying UMeta client."""
        return self._umeta

    def close(self):
        """Close the client connection."""
        if hasattr(self._umeta, "close"):
            self._umeta.close()

    @classmethod
    def reset(cls) -> None:
        """Clear the singleton instance and default client."""
        if cls._instance is not None:
            cls._instance.close()
        cls._instance = None
        cls._default_client = None
