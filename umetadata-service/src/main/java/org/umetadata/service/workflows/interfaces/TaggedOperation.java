package org.umetadata.service.workflows.interfaces;

import org.umetadata.schema.type.EntityReference;

public record TaggedOperation<T>(T operation, EntityReference entityRef) {}
