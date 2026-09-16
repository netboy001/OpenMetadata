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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.umetadata.schema.entity.data.GlossaryTerm;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.EventType;
import org.umetadata.service.Entity;
import org.umetadata.service.jdbi3.GlossaryTermRepository;
import org.umetadata.service.limits.Limits;
import org.umetadata.service.resources.glossary.GlossaryTermMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.security.auth.CatalogSecurityContext;
import org.umetadata.service.util.RestUtil;

@ExtendWith(MockitoExtension.class)
class GlossaryTermToolTest {

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
    GlossaryTermRepository repo = mock(GlossaryTermRepository.class);
    GlossaryTerm glossaryTerm = new GlossaryTerm();
    glossaryTerm.setId(UUID.randomUUID());
    glossaryTerm.setName("TestTerm");

    RestUtil.PutResponse<GlossaryTerm> putResponse =
        new RestUtil.PutResponse<>(Response.Status.CREATED, glossaryTerm, EventType.ENTITY_CREATED);

    when(repo.createOrUpdate(isNull(), any(GlossaryTerm.class), anyString(), any()))
        .thenReturn(putResponse);

    try (MockedStatic<Entity> entityMock = mockStatic(Entity.class);
        MockedConstruction<GlossaryTermMapper> mapperMock =
            mockConstruction(
                GlossaryTermMapper.class,
                (mapper, context) ->
                    when(mapper.createToEntity(any(), anyString())).thenReturn(glossaryTerm))) {

      entityMock.when(() -> Entity.getEntityRepository(Entity.GLOSSARY_TERM)).thenReturn(repo);

      Map<String, Object> params = new HashMap<>();
      params.put("name", "TestTerm");
      params.put("glossary", "TestGlossary");
      params.put("description", "A test term");

      GlossaryTermTool tool = new GlossaryTermTool();
      Map<String, Object> result = tool.execute(authorizer, limits, securityContext, params);

      assertNotNull(result);
      verify(repo).prepareInternal(any(GlossaryTerm.class), eq(false));
    }
  }

  @Test
  void testExecuteWithReviewersCallsGetTeamsOrUsers() {
    GlossaryTermRepository repo = mock(GlossaryTermRepository.class);
    GlossaryTerm glossaryTerm = new GlossaryTerm();
    glossaryTerm.setId(UUID.randomUUID());
    glossaryTerm.setName("TestTerm");

    RestUtil.PutResponse<GlossaryTerm> putResponse =
        new RestUtil.PutResponse<>(Response.Status.CREATED, glossaryTerm, EventType.ENTITY_CREATED);

    when(repo.createOrUpdate(isNull(), any(GlossaryTerm.class), anyString(), any()))
        .thenReturn(putResponse);

    List<EntityReference> reviewerRefs = new ArrayList<>();
    reviewerRefs.add(new EntityReference().withId(UUID.randomUUID()).withType("user"));

    try (MockedStatic<Entity> entityMock = mockStatic(Entity.class);
        MockedStatic<CommonUtils> commonUtilsMock = mockStatic(CommonUtils.class);
        MockedConstruction<GlossaryTermMapper> mapperMock =
            mockConstruction(
                GlossaryTermMapper.class,
                (mapper, context) ->
                    when(mapper.createToEntity(any(), anyString())).thenReturn(glossaryTerm))) {

      entityMock.when(() -> Entity.getEntityRepository(Entity.GLOSSARY_TERM)).thenReturn(repo);
      commonUtilsMock.when(() -> CommonUtils.getTeamsOrUsers(any())).thenReturn(reviewerRefs);

      List<String> reviewers = new ArrayList<>();
      reviewers.add("reviewer-user");

      Map<String, Object> params = new HashMap<>();
      params.put("name", "TestTerm");
      params.put("glossary", "TestGlossary");
      params.put("description", "A test term");
      params.put("reviewers", reviewers);

      GlossaryTermTool tool = new GlossaryTermTool();
      Map<String, Object> result = tool.execute(authorizer, limits, securityContext, params);

      assertNotNull(result);
      verify(repo).prepareInternal(any(GlossaryTerm.class), eq(false));
      commonUtilsMock.verify(() -> CommonUtils.getTeamsOrUsers(any()));
    }
  }
}
