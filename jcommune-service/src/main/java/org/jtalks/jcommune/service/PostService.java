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
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * This interface should have methods which give us more abilities in manipulating Post persistent entity.
 *
 * @author Osadchuck Eugeny
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 */
public interface PostService extends EntityService<Post> {

    /**
     * Update current post with given content, add the modification date.
     *
     * @param post        post to be updated
     * @param postContent content of post
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NotFoundException
     *          when post not found
     */
    void updatePost(Post post, String postContent) throws NotFoundException;

    /**
     * Delete post
     *
     * @param post post to be deleted
     */
    void deletePost(Post post);

    /**
     * Get user's posts.
     *
     * @param userCreated user created post
     * @param page        page number, for which we will find posts
     * @return object that contains posts for one page(note, that one page may contain
     *         all posts, that were created by user) and information for pagination
     */
    Page<Post> getPostsOfUser(JCUser userCreated, String page);

    /**
     * Calculates page number for post based on the current user
     * paging settings and total post amount in the topic
     *
     * @param post post to find a page for
     * @return number of the page where the post will actually be
     */
    int calculatePageForPost(Post post);

    /**
     * Get all posts in the topic of forum.
     *
     * @param topic for this topic we will find posts
     * @param page  page number, for which we will find posts
     * @return object that contains posts for one page(note, that one page may contain
     *         all posts) and information for pagination
     */
    Page<Post> getPosts(Topic topic, String page);

    /**
     * Get the last post, that was posted in a topic of branch.
     *
     * @param branch for this branch it gets the last post
     * @return the last post that was posted in branch
     */
    Post getLastPostFor(Branch branch);

    /**
     * Get the last posts in a topic of branch.
     *
     * @param branch    for this branch it gets the last post
     * @param postCount how many posts to return
     * @return the last post that was posted in branch
     */
    List<Post> getLastPostsFor(Branch branch, int postCount);

    /**
     * Adds comment to post with specified id
     *
     * @param postId id of post to which comment will be added
     * @param properties list of custom comment properties
     * @param body text of the comment

     * @return newly created comment
     *
     * @throws NotFoundException if post with specified id not found
     */
    PostComment addComment(Long postId, Map<String, String> attributes, String body) throws NotFoundException;

    /**
     * Removes specified comment from specified post
     *
     * @param post post form which comment will be removed
     * @param comment comment to remove
     */
    void deleteComment(Post post, PostComment comment);
}
