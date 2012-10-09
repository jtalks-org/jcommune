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

/**
 * View contains information about count of posts to all topics
 *
 * @author masyan
 */
public class ViewCountPostsTopics extends Entity {

    private Topic topic;
    private Branch branch;
    private long postsCount;

    /**
     * @return return {@link Topic}
     */
    public Topic getTopic() {
        return topic;
    }

    /**
     * Set {@link Topic}
     */
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    /**
     * @return return {@link Branch}
     */
    public Branch getBranch() {
        return branch;
    }

    /**
     * Set {@link Branch}
     */
    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    /**
     * Get count of posts to topic
     *
     * @return count of posts
     */
    public long getPostsCount() {
        return postsCount;
    }

    /**
     * Set count of posts
     *
     * @param postsCount count of posts
     */
    public void setPostsCount(long postsCount) {
        this.postsCount = postsCount;
    }
}
