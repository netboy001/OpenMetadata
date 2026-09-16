from metadata.generated.schema.entity.data.table import Table
from _umetadata_testutils.umeta import int_admin_umeta

metadata = int_admin_umeta()
entity = metadata.get_by_name(
    entity=Table, fqn="sample_data.ecommerce_db.shopify.dim_address"
)

if not entity:
    raise ValueError("Table not found")
