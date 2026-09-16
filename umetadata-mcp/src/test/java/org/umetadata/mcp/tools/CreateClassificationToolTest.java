package org.umetadata.mcp.tools;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.Response;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.umetadata.schema.entity.classification.Classification;
import org.umetadata.schema.type.EventType;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.ClassificationRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.tags.ClassificationMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.util.RestUtil;

@ExtendWith(MockitoExtension.class)
class CreateClassificationToolTest {

  private Authorizer authorizer;
  private Limits limits;
  private CatalogSecurityContext securityContext;

  @BeforeEach
  void setUp() {
    authorizer = mock(Authorizer.class);
    limits = mock(Limits.class);
    securityContext = mock(CatalogSecurityContext.class);

    Principal mockPrincipal = mock(Principal.class);
    when(mockPrincipal.getName()).thenReturn("test-user");
    when(securityContext.getUserPrincipal()).thenReturn(mockPrincipal);
  }

  @Test
  void testExecuteCallsPrepareInternal() {
    ClassificationRepository repo = mock(ClassificationRepository.class);
    Classification classification = new Classification();
    classification.setId(UUID.randomUUID());
    classification.setName("PII");

    RestUtil.PutResponse<Classification> putResponse =
        new RestUtil.PutResponse<>(
            Response.Status.CREATED, classification, EventType.ENTITY_CREATED);

    when(repo.createOrUpdate(isNull(), any(Classification.class), anyString(), any()))
        .thenReturn(putResponse);

    try (MockedStatic<Entity> entityMock = mockStatic(Entity.class);
        MockedConstruction<ClassificationMapper> mapperMock =
            mockConstruction(
                ClassificationMapper.class,
                (mapper, context) ->
                    when(mapper.createToEntity(any(), anyString())).thenReturn(classification))) {

      entityMock.when(() -> Entity.getEntityRepository(Entity.CLASSIFICATION)).thenReturn(repo);

      Map<String, Object> params = new HashMap<>();
      params.put("name", "PII");
      params.put("description", "Personally identifiable information");

      CreateClassificationTool tool = new CreateClassificationTool();
      Map<String, Object> result = tool.execute(authorizer, limits, securityContext, params);

      assertNotNull(result);
      verify(repo).prepareInternal(any(Classification.class), eq(false));
    }
  }
}
