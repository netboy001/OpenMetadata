#!/usr/bin/env python3
"""Test connection to UMetadata."""
import sys

# Read token from file
with open("/Users/harsha/Code/UMetadata/scripts/token.txt") as f:
    token = f.read().strip()

print(f"Token length: {len(token)}")
print("Connecting to UMetadata...")

from metadata.generated.schema.security.client.uMetadataJWTClientConfig import (
    UMetadataJWTClientConfig,
)
from metadata.ingestion.umeta.umeta_api import UMetadata
from metadata.generated.schema.entity.services.connections.metadata.uMetadataConnection import (
    UMetadataConnection,
)

server_config = UMetadataConnection(
    hostPort="http://localhost:8585/api",
    securityConfig=UMetadataJWTClientConfig(jwtToken=token),
)

print("Created config, initializing client...")
metadata = UMetadata(server_config)
print("Client initialized!")

# Test a simple API call
print("Testing API call...")
from metadata.generated.schema.entity.services.databaseService import DatabaseService
services = metadata.list_all_entities(entity=DatabaseService, limit=5)
count = 0
for svc in services:
    print(f"  Found service: {svc.name}")
    count += 1
    if count >= 3:
        break

print("Connection successful!")
