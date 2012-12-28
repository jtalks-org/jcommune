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
package org.jtalks.jcommune.service.transactional;

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.service.CodeReviewCommentService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The implementation of {@link CodeReviewCommentService}
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewCommentService extends AbstractTransactionalEntityService<CodeReviewComment, ChildRepository<CodeReviewComment>> 
        implements CodeReviewCommentService {

    /**
     * Create an instance of CodeReview entity based service
     * @param dao               data access object, which should be able do all CRUD operations with entity. 
     */
    public TransactionalCodeReviewCommentService(
                        ChildRepository<CodeReviewComment> dao) {
        super(dao);
    }
 
    /**
     * {@inheritDoc}
     */
    @PreAuthorize("hasPermission(#branchId, 'BRANCH', 'BranchPermission.EDIT_OWN_POSTS') or "+
                  "hasPermission(#branchId, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS')")
    @Override
    public CodeReviewComment updateComment(long id, String body, long branchId) throws NotFoundException {
        CodeReviewComment comment = get(id);
        
        comment.setBody(body);
        getDao().update(comment);
        
        return comment;
    }
}
