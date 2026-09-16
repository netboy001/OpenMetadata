/*
 *  Copyright 2022 Collate
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.umetadata.service.resources.services;

import static org.umetadata.common.utils.CommonUtil.listOrEmpty;
import static org.umetadata.common.utils.CommonUtil.nullOrEmpty;

import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import lombok.Getter;
import org.umetadata.schema.ServiceConnectionEntityInterface;
import org.umetadata.schema.ServiceEntityInterface;
import org.umetadata.schema.entity.services.ServiceType;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.utils.ResultList;
import org.umetadata.service.Entity;
import org.umetadata.service.exception.InvalidServiceConnectionException;
import org.umetadata.service.jdbi3.ListFilter;
import org.umetadata.service.jdbi3.ServiceEntityRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.EntityResource;
import org.umetadata.service.secrets.SecretsManager;
import org.umetadata.service.secrets.SecretsManagerFactory;
import org.umetadata.service.secrets.SecretsUtil;
import org.umetadata.service.secrets.masker.EntityMaskerFactory;
import org.umetadata.service.security.Authorizer;

public abstract class ServiceEntityResource<
        T extends ServiceEntityInterface,
        R extends ServiceEntityRepository<T, S>,
        S extends ServiceConnectionEntityInterface>
    extends EntityResource<T, R> {

  @Getter private final ServiceEntityRepository<T, S> serviceEntityRepository;

  private final ServiceType serviceType;

  protected ServiceEntityResource(
      String entityType, Authorizer authorizer, Limits limits, ServiceType serviceType) {
    super(entityType, authorizer, limits);
    this.serviceType = serviceType;
    serviceEntityRepository =
        (ServiceEntityRepository<T, S>) Entity.getServiceEntityRepository(serviceType);
  }

  protected T decryptOrNullify(SecurityContext securityContext, T service) {
    if (service.getConnection() != null) {
      service
          .getConnection()
          .setConfig(
              retrieveServiceConnectionConfig(
                  service, authorizer.shouldMaskPasswords(securityContext)));
    }
    return service;
  }

  private Object retrieveServiceConnectionConfig(T service, boolean maskPassword) {
    SecretsManager secretsManager = SecretsManagerFactory.getSecretsManager();
    Object config =
        secretsManager.decryptServiceConnectionConfig(
            service.getConnection().getConfig(), extractServiceType(service), serviceType);
    if (maskPassword) {
      config =
          EntityMaskerFactory.getEntityMasker()
              .maskServiceConnectionConfig(config, extractServiceType(service), serviceType);
    }
    return config;
  }

  protected ResultList<T> decryptOrNullify(
      SecurityContext securityContext, ResultList<T> services) {
    listOrEmpty(services.getData()).forEach(service -> decryptOrNullify(securityContext, service));
    return services;
  }

  protected T unmask(T service) {
    // TODO move this functionality to repository
    repository.setFullyQualifiedName(service);
    T originalService =
        repository.findByNameOrNull(service.getFullyQualifiedName(), Include.NON_DELETED);
    String connectionType = extractServiceType(service);
    try {
      if (originalService != null && originalService.getConnection() != null) {
        Object serviceConnectionConfig =
            EntityMaskerFactory.getEntityMasker()
                .unmaskServiceConnectionConfig(
                    service.getConnection().getConfig(),
                    originalService.getConnection().getConfig(),
                    connectionType,
                    serviceType);
        service.getConnection().setConfig(serviceConnectionConfig);
      }
      return service;
    } catch (Exception e) {
      String message =
          SecretsUtil.buildExceptionMessageConnectionMask(e.getMessage(), connectionType, false);
      if (message != null) {
        throw new InvalidServiceConnectionException(message);
      }
      throw InvalidServiceConnectionException.byMessage(
          connectionType,
          String.format("Failed to unmask connection instance of %s", connectionType));
    }
  }

  protected abstract T nullifyConnection(T service);

  protected abstract String extractServiceType(T service);

  protected ResultList<T> listInternal(
      UriInfo uriInfo,
      SecurityContext securityContext,
      String fieldsParam,
      Include include,
      String domain,
      int limitParam,
      String before,
      String after) {
    ListFilter filter = new ListFilter(include);
    if (!nullOrEmpty(domain)) {
      EntityReference domainReference =
          Entity.getEntityReferenceByName(Entity.DOMAIN, domain, Include.NON_DELETED);
      filter.addQueryParam("domainId", String.format("'%s'", domainReference.getId()));
    }
    ResultList<T> services =
        listInternal(uriInfo, securityContext, fieldsParam, filter, limitParam, before, after);
    return addHref(uriInfo, decryptOrNullify(securityContext, services));
  }
}
