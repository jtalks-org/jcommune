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
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * @author Mikhail Stryzhonok
 */
public interface PluginCommentService {

    /**
     * Updates comment's body
     *
     * @param id ID of comment
     * @param body new body of comment
     * @param branchId ID of branch containing code review to check permissions
     *
     * @return updated comment entity
     * @throws NotFoundException when entity not found
     */
    PostComment updateComment(long id, String body, long branchId) throws NotFoundException;

    /**
     * Fetch comment with specified id from database
     *
     * @param id id of interested comment
     *
     * @return comment from with specified id
     * @throws NotFoundException if comment with specified id not found in database
     */
    PostComment getComment(long id) throws NotFoundException;

    /**
     * Marks specified comment of specified post as deleted by  setting deletion date
     *
     * @param post post which contains comment
     * @param comment comment to be delete
     *
     * @return comment marked as deleted
     */
    public PostComment markCommentAsDeleted(Post post, PostComment comment);
}
