/*
 *  Copyright 2021 Collate
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

package org.umetadata.service.apps.bundles.changeEvent;

import static org.umetadata.schema.entity.events.SubscriptionStatus.Status.ACTIVE;
import static org.umetadata.schema.entity.events.SubscriptionStatus.Status.AWAITING_RETRY;
import static org.umetadata.schema.entity.events.SubscriptionStatus.Status.FAILED;

import java.util.Set;
import org.umetadata.common.utils.CommonUtil;
import org.umetadata.schema.entity.events.EventSubscription;
import org.umetadata.schema.entity.events.StatusContext;
import org.umetadata.schema.entity.events.SubscriptionDestination;
import org.umetadata.schema.entity.events.SubscriptionStatus;
import org.umetadata.schema.entity.events.TestDestinationStatus;
import org.umetadata.service.events.errors.EventPublisherException;
import org.umetadata.service.events.subscription.AlertUtil;
import org.umetadata.service.notifications.recipients.context.Recipient;

public interface Destination<T> {
  void sendMessage(T event, Set<Recipient> recipients) throws EventPublisherException;

  void sendTestMessage() throws EventPublisherException;

  /**
   * Indicates whether this destination requires resolved recipients to send notifications.
   * Most destinations (Email, Slack, Teams, etc.) require recipients.
   * Some destinations (ActivityFeed, GovernanceWorkflow) handle their own delivery mechanism
   * and don't use recipient resolution.
   *
   * @return true if recipients must be resolved before sending, false otherwise
   */
  default boolean requiresRecipients() {
    return true;
  }

  SubscriptionDestination getSubscriptionDestination();

  EventSubscription getEventSubscriptionForDestination();

  void close();

  boolean getEnabled();

  default void setErrorStatus(Long attemptTime, Integer statusCode, String reason) {
    setStatus(FAILED, attemptTime, statusCode, reason, null);
  }

  default void setAwaitingRetry(Long attemptTime, int statusCode, String reason) {
    setStatus(AWAITING_RETRY, attemptTime, statusCode, reason, attemptTime + 10);
  }

  default void setSuccessStatus(Long updateTime) {
    SubscriptionStatus subStatus =
        AlertUtil.buildSubscriptionStatus(
            ACTIVE, updateTime, null, null, null, updateTime, updateTime);
    getSubscriptionDestination().setStatusDetails(subStatus);
  }

  default void setStatus(
      SubscriptionStatus.Status status,
      Long attemptTime,
      Integer statusCode,
      String reason,
      Long timestamp) {
    SubscriptionStatus subStatus =
        AlertUtil.buildSubscriptionStatus(
            status, null, attemptTime, statusCode, reason, timestamp, attemptTime);
    getSubscriptionDestination().setStatusDetails(subStatus);
  }

  default void setStatusForTestDestination(
      TestDestinationStatus.Status status, StatusContext statusContext) {
    TestDestinationStatus subStatus = AlertUtil.buildTestDestinationStatus(status, statusContext);
    getSubscriptionDestination().setStatusDetails(subStatus);
  }

  default void setStatusForTestDestination(
      TestDestinationStatus.Status status, Integer statusCode, Long timestamp) {
    TestDestinationStatus subStatus =
        AlertUtil.buildTestDestinationStatus(status, statusCode, timestamp);
    getSubscriptionDestination().setStatusDetails(subStatus);
  }

  default String getDisplayNameOrFqn(EventSubscription eventSubscription) {
    String displayName = eventSubscription.getDisplayName();
    return (CommonUtil.nullOrEmpty(displayName))
        ? eventSubscription.getFullyQualifiedName()
        : displayName;
  }
}
