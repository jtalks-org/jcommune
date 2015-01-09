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
package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * @author Andrei Alikov
 */
public interface PluginPostService {
    /**
     * Gets post with specified id
     *
     * @param id id of interested post
     *
     * @return post with specified id
     *
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NotFoundException if no post with specified id found in database
     */
    Post get(Long id) throws NotFoundException;

    /**
     * Delete post
     *
     * @param post post to be deleted
     */
    void deletePost(Post post);

    /**
     * Update current post with given content, add the modification date.
     *
     * @param post        post to be updated
     * @param postContent content of post
     * @throws org.jtalks.jcommune.plugin.api.exceptions.NotFoundException
     *          when post not found
     */
    void updatePost(Post post, String postContent) throws NotFoundException;
}
