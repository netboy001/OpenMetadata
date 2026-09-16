-- remove uMetadataUrl from smtpSettings
UPDATE umetadata_settings
SET json = JSON_REMOVE(json, '$.uMetadataUrl')
WHERE configType = 'emailConfiguration';