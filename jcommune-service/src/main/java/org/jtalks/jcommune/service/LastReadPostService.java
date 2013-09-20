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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

/**
 * This interface manages last read posts per users and
 * per topics to track whether particular topics has
 * been updated since user's last visit or not.
 *
 * @author Evgeniy Naumenko
 */
public interface LastReadPostService {
        /**
     * Marks topic page as read for the current user.
     * That means all posts on this page are to marked as read.
     * If paging as disabled all posts in the topic will be marked as read.
     * <p/>
     * For anonymous user call will have no effect.
     *
     * @param topic   topic to mark as read
     * @param pageNum page to mark as read
     */
    void markTopicPageAsRead(Topic topic, int pageNum);

    /**
     * Marks the whole topic as read for the current user.
     * That means all posts there are to marked as read.
     * <p/>
     * For anonymous user call will have no effect.
     *
     * @param topic   topic to mark as read
     */
    void markTopicAsRead(Topic topic);

    /**
     * Marks all topics in the current branch as read
     * for the current user.
     *
     * @param branch branch to update topics
     */
    void markAllTopicsAsRead(Branch branch);
    
    /**
     * Mark all forum as read for current user.
     */
    void markAllForumAsReadForCurrentUser();
    
    /**
     * Fills topics with last read post information based
     * on the current user set. No data will be set
     * for anonymous users.
     *
     * @param topics topics to get last read post for
     * @return topic collection with last read posts data set
     */
    List<Topic> fillLastReadPostForTopics(List<Topic> topics);

}
