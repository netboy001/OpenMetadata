package org.umetadata.sdk.fluent.wrappers;

import java.util.ArrayList;
import java.util.List;
import org.umetadata.schema.entity.data.DashboardDataModel;
import org.umetadata.schema.type.TagLabel;
import org.umetadata.sdk.client.UMetadataClient;

/**
 * Fluent wrapper for DashboardDataModel entity to enable method chaining for updates.
 *
 * Usage:
 * <pre>
 * DashboardDataModel updated = new FluentDashboardDataModel(dashboardDataModel, client)
 *     .withDescription("Updated description")
 *     .addTags("tag1", "tag2")
 *     .save();
 * </pre>
 */
public class FluentDashboardDataModel {
  private final DashboardDataModel dashboardDataModel;
  private final UMetadataClient client;
  private boolean modified = false;

  public FluentDashboardDataModel(
      DashboardDataModel dashboardDataModel, UMetadataClient client) {
    this.dashboardDataModel = dashboardDataModel;
    this.client = client;
  }

  /**
   * Update the description.
   */
  public FluentDashboardDataModel withDescription(String description) {
    dashboardDataModel.setDescription(description);
    modified = true;
    return this;
  }

  /**
   * Update the display name.
   */
  public FluentDashboardDataModel withDisplayName(String displayName) {
    dashboardDataModel.setDisplayName(displayName);
    modified = true;
    return this;
  }

  /**
   * Add a tag.
   */
  public FluentDashboardDataModel addTag(String tagFQN) {
    List<TagLabel> tags = dashboardDataModel.getTags();
    if (tags == null) {
      tags = new ArrayList<>();
      dashboardDataModel.setTags(tags);
    }
    tags.add(new TagLabel().withTagFQN(tagFQN).withSource(TagLabel.TagSource.CLASSIFICATION));
    modified = true;
    return this;
  }

  /**
   * Add multiple tags.
   */
  public FluentDashboardDataModel addTags(String... tagFQNs) {
    for (String tagFQN : tagFQNs) {
      addTag(tagFQN);
    }
    return this;
  }

  /**
   * Set custom extension data.
   */
  public FluentDashboardDataModel withExtension(Object extension) {
    dashboardDataModel.setExtension(extension);
    modified = true;
    return this;
  }

  /**
   * Save the changes to the server.
   */
  public DashboardDataModel save() {
    if (!modified) {
      return dashboardDataModel;
    }

    if (dashboardDataModel.getId() == null) {
      throw new IllegalStateException("DashboardDataModel must have an ID to update");
    }

    return client
        .dashboardDataModels()
        .update(dashboardDataModel.getId().toString(), dashboardDataModel);
  }

  /**
   * Get the underlying entity.
   */
  public DashboardDataModel get() {
    return dashboardDataModel;
  }

  /**
   * Check if modified.
   */
  public boolean isModified() {
    return modified;
  }
}
