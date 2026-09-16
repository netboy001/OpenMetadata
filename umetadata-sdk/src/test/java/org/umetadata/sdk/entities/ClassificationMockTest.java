package org.umetadata.sdk.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.umetadata.schema.api.classification.CreateClassification;
import org.umetadata.schema.api.classification.CreateTag;
import org.umetadata.schema.entity.classification.Classification;
import org.umetadata.schema.entity.classification.Tag;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.sdk.client.UMetadataClient;
import org.umetadata.sdk.services.classification.ClassificationService;
import org.umetadata.sdk.services.classification.TagService;

/**
 * Mock tests for Classification and Tag entity operations.
 */
public class ClassificationMockTest {

  @Mock private UMetadataClient mockClient;
  @Mock private ClassificationService mockClassificationService;
  @Mock private TagService mockTagService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockClient.classifications()).thenReturn(mockClassificationService);
    when(mockClient.tags()).thenReturn(mockTagService);
    org.umetadata.sdk.entities.Classification.setDefaultClient(mockClient);
    org.umetadata.sdk.entities.Tag.setDefaultClient(mockClient);
  }

  @Test
  void testCreateClassification() {
    // Arrange
    CreateClassification createRequest = new CreateClassification();
    createRequest.setName("DataClassification");
    createRequest.setDisplayName("Data Classification");
    createRequest.setDescription("Classification for data sensitivity levels");

    Classification expectedClassification = new Classification();
    expectedClassification.setId(UUID.randomUUID());
    expectedClassification.setName("DataClassification");
    expectedClassification.setDisplayName("Data Classification");
    expectedClassification.setFullyQualifiedName("DataClassification");

    when(mockClassificationService.create(any(CreateClassification.class)))
        .thenReturn(expectedClassification);

    // Act
    Classification result = org.umetadata.sdk.entities.Classification.create(createRequest);

    // Assert
    assertNotNull(result);
    assertEquals("DataClassification", result.getName());
    assertEquals("Data Classification", result.getDisplayName());
    verify(mockClassificationService).create(any(CreateClassification.class));
  }

  @Test
  void testCreateTag() {
    // Arrange
    CreateTag createRequest = new CreateTag();
    createRequest.setName("PII");
    createRequest.setDisplayName("Personally Identifiable Information");
    createRequest.setDescription("Contains personal identifying information");
    createRequest.setClassification("DataClassification");

    Tag expectedTag = new Tag();
    expectedTag.setId(UUID.randomUUID());
    expectedTag.setName("PII");
    expectedTag.setDisplayName("Personally Identifiable Information");
    expectedTag.setFullyQualifiedName("DataClassification.PII");

    when(mockTagService.create(any(CreateTag.class))).thenReturn(expectedTag);

    // Act
    Tag result = org.umetadata.sdk.entities.Tag.create(createRequest);

    // Assert
    assertNotNull(result);
    assertEquals("PII", result.getName());
    assertEquals("Personally Identifiable Information", result.getDisplayName());
    assertEquals("DataClassification.PII", result.getFullyQualifiedName());
    verify(mockTagService).create(any(CreateTag.class));
  }

  @Test
  void testRetrieveClassification() {
    // Arrange
    String classificationId = UUID.randomUUID().toString();
    Classification expectedClassification = new Classification();
    expectedClassification.setId(UUID.fromString(classificationId));
    expectedClassification.setName("SecurityClassification");
    expectedClassification.setUsageCount(250);
    expectedClassification.setMutuallyExclusive(true);

    when(mockClassificationService.get(classificationId)).thenReturn(expectedClassification);

    // Act
    Classification result = org.umetadata.sdk.entities.Classification.retrieve(classificationId);

    // Assert
    assertNotNull(result);
    assertEquals(classificationId, result.getId().toString());
    assertEquals("SecurityClassification", result.getName());
    assertEquals(250, result.getUsageCount());
    assertTrue(result.getMutuallyExclusive());
    verify(mockClassificationService).get(classificationId);
  }

  @Test
  void testRetrieveTag() {
    // Arrange
    String tagId = UUID.randomUUID().toString();
    Tag expectedTag = new Tag();
    expectedTag.setId(UUID.fromString(tagId));
    expectedTag.setName("Sensitive");
    expectedTag.setFullyQualifiedName("DataClassification.Sensitive");
    expectedTag.setUsageCount(100);

    when(mockTagService.get(tagId)).thenReturn(expectedTag);

    // Act
    Tag result = org.umetadata.sdk.entities.Tag.retrieve(tagId);

    // Assert
    assertNotNull(result);
    assertEquals(tagId, result.getId().toString());
    assertEquals("Sensitive", result.getName());
    assertEquals(100, result.getUsageCount());
    verify(mockTagService).get(tagId);
  }

  @Test
  void testRetrieveTagWithChildren() {
    // Arrange
    String tagId = UUID.randomUUID().toString();
    String fields = "children,parent,usageCount";
    Tag expectedTag = new Tag();
    expectedTag.setId(UUID.fromString(tagId));
    expectedTag.setName("PII");

    // Mock children tags
    EntityReference childTag1 = new EntityReference();
    childTag1.setName("Email");
    childTag1.setFullyQualifiedName("DataClassification.PII.Email");
    childTag1.setType("tag");
    EntityReference childTag2 = new EntityReference();
    childTag2.setName("PhoneNumber");
    childTag2.setFullyQualifiedName("DataClassification.PII.PhoneNumber");
    childTag2.setType("tag");
    expectedTag.setChildren(List.of(childTag1, childTag2));

    when(mockTagService.get(tagId, fields)).thenReturn(expectedTag);

    // Act
    Tag result = org.umetadata.sdk.entities.Tag.retrieve(tagId, fields);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getChildren());
    assertEquals(2, result.getChildren().size());
    assertEquals("Email", result.getChildren().get(0).getName());
    assertEquals("PhoneNumber", result.getChildren().get(1).getName());
    verify(mockTagService).get(tagId, fields);
  }

  @Test
  void testRetrieveClassificationByName() {
    // Arrange
    String name = "DataQuality";
    Classification expectedClassification = new Classification();
    expectedClassification.setName(name);
    expectedClassification.setFullyQualifiedName(name);

    when(mockClassificationService.getByName(name)).thenReturn(expectedClassification);

    // Act
    Classification result = org.umetadata.sdk.entities.Classification.retrieveByName(name);

    // Assert
    assertNotNull(result);
    assertEquals(name, result.getFullyQualifiedName());
    verify(mockClassificationService).getByName(name);
  }

  @Test
  void testUpdateClassification() {
    // Arrange
    Classification classificationToUpdate = new Classification();
    classificationToUpdate.setId(UUID.randomUUID());
    classificationToUpdate.setName("ComplianceClassification");
    classificationToUpdate.setDescription("Updated compliance classification");
    classificationToUpdate.setMutuallyExclusive(false);

    Classification expectedClassification = new Classification();
    expectedClassification.setId(classificationToUpdate.getId());
    expectedClassification.setName(classificationToUpdate.getName());
    expectedClassification.setDescription(classificationToUpdate.getDescription());
    expectedClassification.setMutuallyExclusive(false);

    when(mockClassificationService.update(
            classificationToUpdate.getId().toString(), classificationToUpdate))
        .thenReturn(expectedClassification);

    // Act
    Classification result =
        org.umetadata.sdk.entities.Classification.update(
            classificationToUpdate.getId().toString(), classificationToUpdate);

    // Assert
    assertNotNull(result);
    assertEquals("Updated compliance classification", result.getDescription());
    assertFalse(result.getMutuallyExclusive());
    verify(mockClassificationService)
        .update(classificationToUpdate.getId().toString(), classificationToUpdate);
  }

  @Test
  void testUpdateTag() {
    // Arrange
    Tag tagToUpdate = new Tag();
    tagToUpdate.setId(UUID.randomUUID());
    tagToUpdate.setName("Confidential");
    tagToUpdate.setDescription("Updated confidential data tag");
    tagToUpdate.setDisabled(false);

    Tag expectedTag = new Tag();
    expectedTag.setId(tagToUpdate.getId());
    expectedTag.setName(tagToUpdate.getName());
    expectedTag.setDescription(tagToUpdate.getDescription());
    expectedTag.setDisabled(false);

    when(mockTagService.update(tagToUpdate.getId().toString(), tagToUpdate))
        .thenReturn(expectedTag);

    // Act
    Tag result =
        org.umetadata.sdk.entities.Tag.update(tagToUpdate.getId().toString(), tagToUpdate);

    // Assert
    assertNotNull(result);
    assertEquals("Updated confidential data tag", result.getDescription());
    assertFalse(result.getDisabled());
    verify(mockTagService).update(tagToUpdate.getId().toString(), tagToUpdate);
  }

  @Test
  void testDeleteClassification() {
    // Arrange
    String classificationId = UUID.randomUUID().toString();
    doNothing().when(mockClassificationService).delete(eq(classificationId), any());

    // Act
    org.umetadata.sdk.entities.Classification.delete(classificationId);

    // Assert
    verify(mockClassificationService).delete(eq(classificationId), any());
  }

  @Test
  void testDeleteTag() {
    // Arrange
    String tagId = UUID.randomUUID().toString();
    doNothing().when(mockTagService).delete(eq(tagId), any());

    // Act
    org.umetadata.sdk.entities.Tag.delete(tagId);

    // Assert
    verify(mockTagService).delete(eq(tagId), any());
  }
}
