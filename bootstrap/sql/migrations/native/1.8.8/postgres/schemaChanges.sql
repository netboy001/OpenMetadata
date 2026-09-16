-- Add migrations to fetch updated searchSettings in 1.8.7
DELETE FROM umetadata_settings WHERE configType = 'searchSettings';

