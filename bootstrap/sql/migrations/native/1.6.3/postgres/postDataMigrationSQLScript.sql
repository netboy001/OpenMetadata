-- remove uMetadataUrl from smtpSettings
UPDATE umetadata_settings
SET json = json - 'uMetadataUrl'
WHERE configType = 'emailConfiguration';