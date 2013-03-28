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
package org.jtalks.jcommune.model.dao;

import java.util.List;

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;

/**
 * Interface allows to make basic CRUD operations with the
 * {@link LastReadPost} objects.
 * 
 * @author Evgeniy Naumenko
 * @author Anuar_Nurmakanov
 */
public interface LastReadPostDao extends ChildRepository<LastReadPost> {

    /**
     * Returns all last read post data saved for the topic given
     * for all all the users.
     *
     * @param topic topic to find last read posts for
     * @return last read post data for all the users
     */
    List<LastReadPost> getLastReadPostsInTopic(Topic topic);

    /**
     * Fetches last read post information for particular user and topic.
     *
     * @param forWhom user to find last read post for
     * @param topic  topic we're interesting in
     * @return last read post for the particular topic or null if user had never opened this topic
     */
    LastReadPost getLastReadPost(JCUser forWhom, Topic topic);
    
    /**
     * Get last read posts of user in the list of topics.
     * 
     * @param forWhom for this user it founds the list of last read posts
     * @param sourceTopics in this list of topics we need to find last read posts
     * @return last read posts of user in the list of topics
     */
    List<LastReadPost> getLastReadPosts(JCUser forWhom, List<Topic> sourceTopics);

    /**
     * Mark all topics as read.
     *
     * @param forWhom user to find last read post for
     * @param branch branch contained topics to mark
     */
    void markAllRead(JCUser forWhom, Branch branch);
    
    /**
     * Delete all last read post records for given user.
     * 
     * @param user for this user we delete all records that contain
     *        an information about last read post
     */
    void deleteLastReadPostsFor(JCUser user);
}
