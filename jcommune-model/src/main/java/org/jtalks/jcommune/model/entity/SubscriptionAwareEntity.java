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

import org.jtalks.common.model.entity.Entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulates subscription management details.
 * All the entities with subscription allowed should extend this class instead
 * of implementing subscription handling on their own.
 */
public abstract class SubscriptionAwareEntity extends Entity {

    private Set<JCUser> subscribers = new HashSet<JCUser>();

    /**
     * Returns users subscribed to get email notifications
     * about this entity's updates
     *
     * @return users to send notifications on update to
     */
    public Set<JCUser> getSubscribers() {
        return subscribers;
    }

    /**
     * Sets subscribers list for this branch.
     * For Hibernate use only.
     *
     * @param subscribers users to send notifications on update to
     */
    protected void setSubscribers(Set<JCUser> subscribers) {
        this.subscribers = subscribers;
    }
}
