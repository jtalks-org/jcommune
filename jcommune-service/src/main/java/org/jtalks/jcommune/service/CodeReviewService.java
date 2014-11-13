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

import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;

/**
 * The interface to manipulate with code reviews
 *
 * @author Vyacheslav Mishcheryakov
 */
public interface CodeReviewService extends EntityService<CodeReview> {

    /**
     * Add code review (CR) comment
     *
     * @param reviewId   - ID of code review where add comment to
     * @param lineNumber - number of code line for comment
     * @param body       - message body
     * @return created CR comment entity
     * @throws NotFoundException     if CR was not found
     * @throws org.springframework.security.access.AccessDeniedException when user has no permission to add comment
     */
    PostComment addComment(Long reviewId, int lineNumber, String body)
            throws NotFoundException;

    /**
     * Removes code review (CR) comment
     *
     * @param reviewComment Code review comment
     * @param codeReview    Code review where needs to delete comment
     * @throws org.springframework.security.access.AccessDeniedException when user has no permission to add comment
     */
    void deleteComment(PostComment reviewComment, CodeReview codeReview);
}
