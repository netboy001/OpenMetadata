package org.umetadata.service.resources.docstore;

import jakarta.ws.rs.core.Response;
import org.umetadata.schema.email.EmailTemplate;
import org.umetadata.schema.email.TemplateValidationResponse;
import org.umetadata.schema.entities.docStore.CreateDocument;
import org.umetadata.schema.entities.docStore.Document;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.Entity;
import org.umetadata.service.exception.CustomExceptionMessage;
import org.umetadata.service.jdbi3.DocumentRepository;
import org.umetadata.service.mapper.EntityMapper;
import org.umetadata.service.security.Authorizer;
import org.umetadata.service.util.email.DefaultTemplateProvider;

public class DocStoreMapper implements EntityMapper<Document, CreateDocument> {
  public DocStoreMapper(Authorizer authorizer) {
    this.authorizer = authorizer;
  }

  private final Authorizer authorizer;

  @Override
  public Document createToEntity(CreateDocument create, String user) {
    DocumentRepository documentRepository =
        (DocumentRepository) Entity.getEntityRepository(Entity.DOCUMENT);
    // Validate email template
    if (create.getEntityType().equals(DefaultTemplateProvider.ENTITY_TYPE_EMAIL_TEMPLATE)) {
      // Only Admins Can do these operations
      authorizer.authorizeAdmin(user);
      String content = JsonUtils.convertValue(create.getData(), EmailTemplate.class).getTemplate();
      TemplateValidationResponse validationResp =
          documentRepository.validateEmailTemplate(create.getName(), content);
      if (Boolean.FALSE.equals(validationResp.getIsValid())) {
        throw new CustomExceptionMessage(
            Response.status(400).entity(validationResp).build(), validationResp.getMessage());
      }
    }
    return copy(new Document(), create, user)
        .withFullyQualifiedName(create.getFullyQualifiedName())
        .withData(create.getData())
        .withEntityType(create.getEntityType());
  }
}
