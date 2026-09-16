# Metadata

Messaging Service Metadata Pipeline Configuration.

## Configuration

$$section

### Topic Filter Pattern $(id="topicFilterPattern")

Topic filter patterns are used to control whether to include Topics as part of metadata ingestion.

**Include**: Explicitly include Topics by adding a list of regular expressions to the `Include` field. UMetadata will include all Topics with names matching one or more of the supplied regular expressions. All other Topics will be excluded.

For example, to include only those Topics whose name starts with the word `demo`, add the regex pattern in the include field as `^demo.*`.

**Exclude**: Explicitly exclude Topics by adding a list of regular expressions to the `Exclude` field. UMetadata will exclude all Topics with names matching one or more of the supplied regular expressions. All other Topics will be included.

For example, to exclude all Topics with the name containing the word `demo`, add regex pattern in the exclude field as `.*demo.*`.

Checkout <a href="https://docs.u-metadata.org/connectors/ingestion/workflows/metadata/filter-patterns/database#database-filter-pattern" target="_blank">this</a> document for further examples on filter patterns.
$$

$$section
### Ingest Sample Data $(id="generateSampleData")

Set the Ingest Sample Data toggle to control whether to ingest sample data as part of the metadata ingestion.
$$

$$section
### Enable Debug Logs $(id="enableDebugLog")

Set the `Enable Debug Log` toggle to set the logging level of the process to debug. You can check these logs in the Ingestion tab of the service and dig deeper into any errors you might find.
$$

$$section
### Mark Deleted Topics $(id="markDeletedTopics")

Optional configuration to soft delete `topics` in UMetadata if the source `topics` are deleted. After deleting, all the associated entities like lineage, etc., with that `topic` will be deleted.
$$

$$section
### Override Metadata $(id="overrideMetadata")

Set the `Override Metadata` toggle to control whether to override the existing metadata in the UMetadata server with the metadata fetched from the source.

If the toggle is `enabled`, the metadata fetched from the source will override and replace the existing metadata in the UMetadata.

If the toggle is `disabled`, the metadata fetched from the source will not override the existing metadata in the UMetadata server. In this case the metadata will only get updated for fields that has no value added in UMetadata.

This is applicable for fields like description, tags, owner and displayName

$$

$$section
### Number of Retries $(id="retries")

Times to retry the workflow in case it ends with a failure.
$$

$$section
### Raise on Error $(id="raiseOnError")

Mark the workflow as failed or avoid raising exceptions.
$$