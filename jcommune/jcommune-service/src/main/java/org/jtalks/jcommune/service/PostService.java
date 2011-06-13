/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.Post;

import java.util.List;

/**
 * This interface should have methods which give us more abilities in manipulating Post persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public interface PostService extends EntityService<Post> {
    /**
     * Get posts range from topic.
     *
     * @param topicId topic id from which we obtain posts
     * @param start   start index of post
     * @param max     number of posts
     * @return list of {@code Topic} objects with size {@code max}
     */
    List<Post> getPostRangeInTopic(long topicId, int start, int max);

    /**
     * Get number of posts in topic.
     *
     * @param topicId topic id where you have to count posts
     * @return number of posts in topic
     */
    int getPostsInTopicCount(long topicId);
}
