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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

import java.util.List;

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
     * @param post post to find a page for
     * @return number of the page where the post will actually be
     */
    int calculatePageForPost(Post post);
}
