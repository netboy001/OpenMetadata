package org.umetadata.service.resources;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.util.List;
import lombok.Getter;
import org.umetadata.schema.EntityTimeSeriesInterface;
import org.umetadata.schema.utils.ResultList;
import org.umetadata.service.Entity;
import org.umetadata.service.UMetadataApplicationConfig;
import org.umetadata.service.jdbi3.EntityTimeSeriesRepository;
import org.umetadata.service.search.SearchListFilter;
import org.umetadata.service.search.SearchSortFilter;
import org.umetadata.service.security.AuthRequest;
import org.umetadata.service.security.AuthorizationLogic;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.policyevaluator.OperationContext;
import org.umetadata.service.security.policyevaluator.ResourceContextInterface;
import org.umetadata.service.util.EntityUtil;

public abstract class EntityTimeSeriesResource<
    T extends EntityTimeSeriesInterface, K extends EntityTimeSeriesRepository<T>> {
  protected final Class<T> entityClass;
  protected final String entityType;
  @Getter protected final K repository;
  protected final Authorizer authorizer;

  public EntityTimeSeriesResource(String entityType, Authorizer authorizer) {
    this.entityType = entityType;
    this.entityClass = (Class<T>) Entity.getEntityClassFromType(entityType);
    this.repository = (K) Entity.getEntityTimeSeriesRepository(entityType);
    this.authorizer = authorizer;
    Entity.registerTimeSeriesResourcePermissions(entityType);
  }

  public void initialize(UMetadataApplicationConfig config) {
    // Nothing to do in the default implementation
  }

  protected Response create(T entity, String extension, String recordFQN) {
    entity = repository.createNewRecord(entity, extension, recordFQN);
    return Response.ok(entity).build();
  }

  protected Response create(T entity, String recordFQN) {
    entity = repository.createNewRecord(entity, recordFQN);
    return Response.ok(entity).build();
  }

  protected ResultList<T> listInternalFromSearch(
      SecurityContext securityContext,
      EntityUtil.Fields fields,
      SearchListFilter searchListFilter,
      int limit,
      int offset,
      SearchSortFilter searchSortFilter,
      String q,
      String queryString,
      OperationContext operationContext,
      ResourceContextInterface resourceContext)
      throws IOException {
    authorizer.authorize(securityContext, operationContext, resourceContext);
    return repository.listFromSearchWithOffset(
        fields, searchListFilter, limit, offset, searchSortFilter, q, queryString);
  }

  protected ResultList<T> listInternalFromSearch(
      SecurityContext securityContext,
      EntityUtil.Fields fields,
      SearchListFilter searchListFilter,
      int limit,
      int offset,
      SearchSortFilter searchSortFilter,
      String q,
      String queryString,
      List<AuthRequest> authRequests,
      AuthorizationLogic authorizationLogic)
      throws IOException {
    authorizer.authorizeRequests(securityContext, authRequests, authorizationLogic);
    return repository.listFromSearchWithOffset(
        fields, searchListFilter, limit, offset, searchSortFilter, q, queryString);
  }

  public ResultList<T> listLatestFromSearch(
      SecurityContext securityContext,
      EntityUtil.Fields fields,
      SearchListFilter searchListFilter,
      String groupBy,
      String q,
      Integer limit,
      Integer offset,
      String sortField,
      String sortType,
      OperationContext operationContext,
      ResourceContextInterface resourceContext)
      throws IOException {
    authorizer.authorize(securityContext, operationContext, resourceContext);
    return repository.listLatestFromSearch(
        fields, searchListFilter, groupBy, q, limit, offset, sortField, sortType);
  }

  public ResultList<T> listLatestFromSearch(
      SecurityContext securityContext,
      EntityUtil.Fields fields,
      SearchListFilter searchListFilter,
      String groupBy,
      String q,
      Integer limit,
      Integer offset,
      String sortField,
      String sortType,
      List<AuthRequest> authRequests,
      AuthorizationLogic authorizationLogic)
      throws IOException {
    authorizer.authorizeRequests(securityContext, authRequests, authorizationLogic);
    return repository.listLatestFromSearch(
        fields, searchListFilter, groupBy, q, limit, offset, sortField, sortType);
  }

  protected T latestInternalFromSearch(
      SecurityContext securityContext,
      EntityUtil.Fields fields,
      SearchListFilter searchListFilter,
      String q,
      OperationContext operationContext,
      ResourceContextInterface resourceContext)
      throws IOException {
    authorizer.authorize(securityContext, operationContext, resourceContext);
    return repository.latestFromSearch(fields, searchListFilter, q);
  }

  protected T latestInternalFromSearch(
      SecurityContext securityContext,
      EntityUtil.Fields fields,
      SearchListFilter searchListFilter,
      String q,
      List<AuthRequest> authRequests,
      AuthorizationLogic authorizationLogic)
      throws IOException {
    authorizer.authorizeRequests(securityContext, authRequests, authorizationLogic);
    return repository.latestFromSearch(fields, searchListFilter, q);
  }
}
