package org.umetadata.service.resources.feeds;

import static org.umetadata.common.utils.CommonUtil.listOrEmpty;

import jakarta.ws.rs.core.Response;
import java.util.UUID;
import org.umetadata.schema.api.feed.CreateSuggestion;
import org.umetadata.schema.entity.feed.Suggestion;
import org.umetadata.schema.type.Include;
import org.umetadata.schema.type.SuggestionStatus;
import org.umetadata.schema.type.SuggestionType;
import org.umetadata.schema.type.TagLabel;
import org.umetadata.sdk.exception.SuggestionException;
import org.umetadata.service.Entity;
import org.umetadata.service.resources.tags.TagLabelUtil;
import org.umetadata.service.util.UserUtil;

public class SuggestionMapper {
  private final String INVALID_SUGGESTION_REQUEST = "INVALID_SUGGESTION_REQUEST";

  public Suggestion createToEntity(CreateSuggestion create, String user) {
    validate(create);
    return new Suggestion()
        .withId(UUID.randomUUID())
        .withDescription(create.getDescription())
        .withEntityLink(create.getEntityLink())
        .withType(create.getType())
        .withDescription(create.getDescription())
        .withTagLabels(create.getTagLabels())
        .withStatus(SuggestionStatus.Open)
        .withCreatedBy(UserUtil.getUserOrBot(user))
        .withCreatedAt(System.currentTimeMillis())
        .withUpdatedBy(user)
        .withUpdatedAt(System.currentTimeMillis());
  }

  private void validate(CreateSuggestion suggestion) {
    if (suggestion.getEntityLink() == null) {
      throw new SuggestionException(
          Response.Status.BAD_REQUEST,
          INVALID_SUGGESTION_REQUEST,
          "Suggestion's entityLink cannot be null.");
    }
    MessageParser.EntityLink entityLink =
        MessageParser.EntityLink.parse(suggestion.getEntityLink());
    Entity.getEntityReferenceByName(
        entityLink.getEntityType(), entityLink.getEntityFQN(), Include.NON_DELETED);

    if (suggestion.getType() == SuggestionType.SuggestDescription) {
      if (suggestion.getDescription() == null || suggestion.getDescription().isEmpty()) {
        throw new SuggestionException(
            Response.Status.BAD_REQUEST,
            INVALID_SUGGESTION_REQUEST,
            "Suggestion's description cannot be empty.");
      }
    } else if (suggestion.getType() == SuggestionType.SuggestTagLabel) {
      if (suggestion.getTagLabels().isEmpty()) {
        throw new SuggestionException(
            Response.Status.BAD_REQUEST,
            INVALID_SUGGESTION_REQUEST,
            "Suggestion's tag label's cannot be empty.");
      } else {
        for (TagLabel label : listOrEmpty(suggestion.getTagLabels())) {
          TagLabelUtil.applyTagCommonFields(label);
        }
      }
    } else {
      throw new SuggestionException(
          Response.Status.BAD_REQUEST, INVALID_SUGGESTION_REQUEST, "Invalid Suggestion Type.");
    }
  }
}
