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

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Topic;

import java.util.List;

/**
 * @author Evgeniy Naumenko
 */
public interface LastReadPostDao extends ChildRepository<LastReadPost> {

    /**
     * Returns all last read post data saved for the topic given
     * for all all the users.
     *
     * @param topic topic to find last read posts for
     * @return last read post data for all the users
     */
    List<LastReadPost> listLastReadPostsForTopic(Topic topic);

    /**
     * Fetches last read post information for particular user and topic.
     *
     * @param forWho user to find last read post for
     * @param topic  topic we're interesting in
     * @return last read post for the particular topic or null if user had never opened this topic
     */
    LastReadPost getLastReadPost(JCUser forWho, Topic topic);

    /**
     * Mark all topics as read.
     *
     * @param forWho user to find last read post for
     * @param branch branch contained topics to mark
     */
    void markAllRead(JCUser forWho, Branch branch);

}
