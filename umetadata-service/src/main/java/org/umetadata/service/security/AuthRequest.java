package org.umetadata.service.security;

import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.security.policyevaluator.ResourceContextInterface;

public record AuthRequest(
    OperationContext operationContext, ResourceContextInterface resourceContext) {}
