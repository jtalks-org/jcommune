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

import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * The interface to manipulate with code reviews
 * 
 * @author Vyacheslav Mishcheryakov
 */
public interface PostCommentService extends EntityService<PostComment> {

    /**
     * Updates CR comment's body
     * @param id ID of CR comment
     * @param body new body of comment
     * @param branchId ID of branch containing code review to check permissions
     * @return updated CR comment entity
     * @throws NotFoundException when entity not found
     */
    PostComment updateComment(long id, String body, long branchId) throws NotFoundException;

}
