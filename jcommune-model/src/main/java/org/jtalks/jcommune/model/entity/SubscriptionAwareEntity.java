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
     * Sets subscribers list for this branch. For Hibernate use only.
     *
     * @param subscribers users to send notifications on update to
     */
    void setSubscribers(Set<JCUser> subscribers);

    /**
     * If user wants to see subscription updates she will get a notification with a link to particular forum location.
     * This method prepares a URL suffix to this location. Example: http://javatalks.ru/{url_suffix}/12. Url suffix is
     * different for branches, topics, code reviews, etc.
     *
     * @return URL suffix
     */
    String prepareUrlSuffix();

}
