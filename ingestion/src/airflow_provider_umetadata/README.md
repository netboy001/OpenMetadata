# UMetadata Airflow Provider

This package brings:
- Lineage Backend
- Lineage Operator
- UMetadata Hook

Note that this is configured as an entrypoint in the `setup.py`:

```python
entry_points={
    "apache_airflow_provider": [
        "provider_info = airflow_provider_umetadata:get_provider_config"
    ],
},
```

Therefore, any metadata changes that should be discoverable by Airflow need to be passed in `get_provider_config`.

More information about that on Airflow's [docs](https://airflow.apache.org/docs/apache-airflow-providers/index.html?utm_cta=website-events-featured-summit#creating-your-own-providers).

## How to use the UMetadataHook

In the Airflow UI you can create a new UMetadata connection.

Then, load it as follows:

```python
from airflow_provider_umetadata.hooks.umetadata import UMetadataHook

umetadata_hook = UMetadataHook(umetadata_conn_id="om_id")  # The ID you provided
server_config = umetadata_hook.get_conn()
```

## How to use the UMetadataLineageOperator

```python
from airflow_provider_umetadata.lineage.operator import UMetadataLineageOperator

UMetadataLineageOperator(
    task_id='lineage_op',
    depends_on_past=False,
    server_config=server_config,
    service_name="your-airflow-service",
    only_keep_dag_lineage=True,
)
```

You can get the `server_config` variable using the `UMetadataHook` as shown above, or create it
directly:

```python
from metadata.generated.schema.entity.services.connections.metadata.uMetadataConnection import (
    UMetadataConnection,
)
from metadata.generated.schema.security.client.uMetadataJWTClientConfig import (
    UMetadataJWTClientConfig,
)

server_config = UMetadataConnection(
    hostPort="http://localhost:8585/api",
    authProvider="umetadata",
    securityConfig=UMetadataJWTClientConfig(
        jwtToken="<token>"
    ),
)
```
