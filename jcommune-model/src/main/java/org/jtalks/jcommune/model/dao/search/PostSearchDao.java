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
package org.jtalks.jcommune.model.dao.search;

import java.util.List;

import org.jtalks.jcommune.model.entity.Post;

/**
 * This interface describes the contract of the DAO for full-text posts search.
 * 
 * @author Anuar Nurmakanov
 * @see org.jtalks.jcommune.model.dao.search.hibernate.PostHibernateSearchDao
 */
public interface PostSearchDao extends SearchDao<Post>{
	/**
     * Search posts for their content.
     * 
     * @param searchText contents of the post.
     * @return list of posts
     */
    List<Post> searchPosts(String searchText);
}
