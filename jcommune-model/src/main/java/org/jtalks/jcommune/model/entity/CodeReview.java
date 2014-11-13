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

import java.util.Set;

/**
 * Represents the code review for the topic. Contains the list of {@link PostComment}
 * for each commented line of code and configuration parameters for review.
 *
 * @author Vyacheslav Mishcheryakov
 */
public class CodeReview extends Entity implements SubscriptionAwareEntity {

    public static final String URL_SUFFIX = "/topics/";
    private Topic topic;


    /**
     * {@inheritDoc}
     */
    public Set<JCUser> getSubscribers() {
        return topic.getSubscribers();
    }

    /**
     * {@inheritDoc}
     */
    public void setSubscribers(Set<JCUser> subscribers) {
        topic.setSubscribers(subscribers);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p/>
     * The target URL has the next format http://{forum root}/topics/{id}
     */
    @Override
    public String prepareUrlSuffix() {
        return URL_SUFFIX + topic.getId();
    }

    /**
     * Check if user subscribed to {@link CodeReview}.
     *
     * @param user {@link JCUser} to check.
     * @return {@code true} if user subscribed to {@link CodeReview} otherwise {@code false}.
     */
    public boolean isUserSubscribed(JCUser user) {
        return topic.userSubscribed(user);
    }

    /**
     * @return the topic
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * @param topic the topic to set
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * Get post where code review is placed.
     *
     * @return post where code review is placed
     */
    public Post getOwnerPost() {
        return getTopic().getFirstPost();
    }
}
