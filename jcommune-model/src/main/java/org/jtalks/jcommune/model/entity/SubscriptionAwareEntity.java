/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.entity;

import java.util.Set;

/**
 * Encapsulates subscription management details. All the entities with subscription allowed should implement this
 * interface instead of implementing a custom subscription handling on their own.
 */
public interface SubscriptionAwareEntity {

    /**
     * Returns users subscribed to get email notifications about this entity's updates
     *
     * @return users to send notifications on update to
     */
    Set<JCUser> getSubscribers();

    /**
     * If user wants to see subscription updates she will get a notification with a link to particular forum location.
     * This method prepares a URL suffix to this location. Example: http://javatalks.ru/{url_suffix}/12. Url suffix is
     * different for branches, topics, code reviews, etc.
     *
     * @return URL suffix
     */
    String getUrlSuffix();

    /**
     * Gets link for unsubscription form updates of entity for subscribes of entity of specified class.
     * Sometimes if entity updates we need to send notifications to subscribers of parent entity (if topic
     * moved we need to notify target branch subscribers) so in this case we should show different urls
     * for subscribers of branch and subscribers of topic
     *
     * @param clazz class of entity user subscribed to
     * @param <T> subclass of {@link org.jtalks.jcommune.model.entity.SubscriptionAwareEntity}
     *
     * @return link for unsubscription
     */
    <T extends SubscriptionAwareEntity> String getUnsubscribeLinkForSubscribersOf(Class<T> clazz);
}
