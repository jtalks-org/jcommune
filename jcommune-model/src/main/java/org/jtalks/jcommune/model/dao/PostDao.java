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
import org.jtalks.jcommune.model.dto.JcommunePageable;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.data.domain.Page;


/**
 * Interface allows to make basic CRUD operations with the
 * {@link Post} objects.
 * At the current moment it doesn't provides any additional methods over the basic {@link ChildRepository} interface
 * but some specific methods will be added soon.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Anuar Nurmakanov
 * @see org.jtalks.jcommune.model.dao.hibernate.PostHibernateDao
 */
public interface PostDao extends ChildRepository<Post> {

    /**
     * Get all the posts that were created  by user.
     * 
     * @param author user to select posts for
     * @param pageRequest contains information for pagination: page number, page size
     * @param pagingEnabled if true, then it returns posts for one page, otherwise it
     *        return all posts, that were created by user
     * @return object that contains posts for one page(note, that one page may contain
     *         all posts, that were created by user) and information for pagination
     */
    Page<Post> getUserPosts(JCUser author, JcommunePageable pageRequest, boolean pagingEnabled);

    /**
     * Find the latest post in the forum topic.
     * 
     * @param topic the topic, in which we try to find
     * @return the latest post in the forum topic
     */
    Post getLastPostInTopic(Topic topic);
    
    /**
     * Get all posts in the topic of forum.
     * 
     * @param topic for this topic we will find posts
     * @param pageRequest contains information for pagination: page number, page size
     * @param pagingEnabled if true, then it returns posts for one page, otherwise it
     *        return all posts in the topic 
     * @return object that contains posts for one page(note, that one page may contain
     *         all posts) and information for pagination
     */
    Page<Post> getPosts(Topic topic, JcommunePageable pageRequest, boolean pagingEnabled);
}
