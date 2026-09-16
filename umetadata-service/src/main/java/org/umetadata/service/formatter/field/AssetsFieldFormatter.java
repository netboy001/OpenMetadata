package org.umetadata.service.formatter.field;

import static org.umetadata.service.Entity.FIELD_ASSETS;

import org.umetadata.schema.entity.feed.AssetsFeedInfo;
import org.umetadata.schema.entity.feed.FeedInfo;
import org.umetadata.schema.entity.feed.Thread;
import org.umetadata.schema.type.EntityReference;
import org.umetadata.schema.type.FieldChange;
import org.umetadata.schema.utils.JsonUtils;
import org.umetadata.service.formatter.decorators.MessageDecorator;

public class AssetsFieldFormatter extends DefaultFieldFormatter {
  private static final String HEADER_MESSAGE = "%s %s the assets in %s %s";

  public AssetsFieldFormatter(
      MessageDecorator<?> messageDecorator, Thread thread, FieldChange fieldChange) {
    super(messageDecorator, thread, fieldChange);
  }

  @Override
  public String formatAddedField() {
    String message = getHeaderForAssetsUpdate(Thread.FieldOperation.ADDED.value());
    // Update the thread with the required information
    populateAssetsFeedInfo(Thread.FieldOperation.ADDED, message);
    return message;
  }

  @Override
  public String formatDeletedField() {
    String message = getHeaderForAssetsUpdate(Thread.FieldOperation.DELETED.value());
    // Update the thread with the required information
    populateAssetsFeedInfo(Thread.FieldOperation.DELETED, message);
    return message;
  }

  private void populateAssetsFeedInfo(Thread.FieldOperation operation, String threadMessage) {
    AssetsFeedInfo assetsFeedInfo =
        new AssetsFeedInfo()
            .withUpdatedAssets(
                JsonUtils.readOrConvertValues(fieldChange.getNewValue(), EntityReference.class));
    FeedInfo feedInfo =
        new FeedInfo()
            .withHeaderMessage(threadMessage)
            .withFieldName(FIELD_ASSETS)
            .withEntitySpecificInfo(assetsFeedInfo);
    populateThreadFeedInfo(thread, threadMessage, Thread.CardStyle.ASSETS, operation, feedInfo);
  }

  private String getHeaderForAssetsUpdate(String opMessage) {
    return String.format(
        HEADER_MESSAGE,
        thread.getUpdatedBy(),
        opMessage,
        thread.getEntityRef().getType(),
        thread.getEntityUrlLink());
  }
}
