-- Fix emailConfiguration templates field for users affected by v1.8.0 bug
-- See commit: https://10.1.98.30:5080/umd/umetadata/UMetadata/commit/a50cddf665544b0934ce79a539aecbb00bd541c8
-- This migration ensures all emailConfiguration records have the correct 'umetadata' value
UPDATE umetadata_settings
SET json = jsonb_set(json, '{templates}', '"umetadata"')
WHERE configType = 'emailConfiguration';