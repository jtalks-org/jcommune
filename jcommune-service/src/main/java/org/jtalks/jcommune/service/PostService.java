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

import org.jtalks.jcommune.model.entity.LastReadPost;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

import java.util.List;
import java.util.Map;

/**
 * This interface should have methods which give us more abilities in manipulating Post persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 */
public interface PostService extends EntityService<Post> {


    /**
     * Update current post with given content, add the modification date.
     *
     * @param postId      post id
     * @param postContent content of post
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when post not found
     */
    void updatePost(long postId, String postContent) throws NotFoundException;

    /**
     * Delete post  by id.
     *
     * @param postId post id
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    void deletePost(long postId) throws NotFoundException;

    /**
     * List posts of user
     *
     * @param userCreated user created post
     * @return post list
     */
    List<Post> getPostsOfUser(JCUser userCreated);

    /**
     * Calculates page number for post based on the current user
     * paging settings and total post amount in the topic
     *
     * @param post post to find a page for
     * @return number of the page where the post will actually be
     */
    int calculatePageForPost(Post post);

    /**
     * Fills topics with last read post information based
     * on the current user set. No data will be set
     * for anonymous users.
     *
     * @param topics topics to get last read post for
     * @return topic collection with last read posts data set
     */
    List<Topic> fillLastReadPostForTopics(List<Topic> topics);

    /**
     * Returns last read post for topic or null, if there is no
     * last read post for the current user and topic given.
     * <p/>
     * Will allways return null for anonymous users.
     *
     * @param topic topics to get last read post for
     * @return last read post info for these topics and current user
     */
    LastReadPost getLastReadPostForTopic(Topic topic);
}
