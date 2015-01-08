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

import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * Interface allows to make basic CRUD operations with the
 * {@link Post} objects.
 * At the current moment it doesn't provides any additional methods over the basic {@link Crud} interface
 * but some specific methods will be added soon.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 * @see org.jtalks.jcommune.model.dao.hibernate.PostHibernateDao
 */
public interface PostDao extends Crud<Post> {

    /**
     * Get all the posts that were created  by user.
     * 
     * @param author user to select posts for
     * @param pageRequest contains information for pagination: page number, page size
     * @param allowedBranchesIds list of allowed branches id
     * @return object that contains posts for one page(note, that one page may contain
     *         all posts, that were created by user) and information for pagination
     */
    Page<Post> getUserPosts(JCUser author, PageRequest pageRequest, List<Long> allowedBranchesIds);

    /**
     * Get all posts in the topic of forum.
     * 
     * @param topic for this topic we will find posts
     * @param pageRequest contains information for pagination: page number, page size
     * @return object that contains posts for one page(note, that one page may contain
     *         all posts) and information for pagination
     */
    Page<Post> getPosts(Topic topic, PageRequest pageRequest);
    
    /**
     * Get last post that was posted in a topic of branch.
     * 
     * @param branch in this branch post was posted
     * @return last post that was posted in a topic of branch
     */
    Post getLastPostFor(Branch branch);

    /**
     * Get last posts that were posted in a topics of branches.
     *
     * @param branchIds in those branches posts were posted
     * @param postCount how many posts to return
     * @return last posts that were posted in a topics of branch
     */
    List<Post> getLastPostsFor(List<Long> branchIds, int postCount);

    /**
     * Changes rating of post with specified id by specified value
     *
     * @param postId id of the post to change rating
     * @param changes value of rating changes
     *
     */
    void changeRating(Long postId, int changes);

}
