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

import org.joda.time.DateTime;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.dao.CodeReviewDao;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.PermissionService;

/**
 * The implementation of (@link {@link CodeReviewService}
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewService extends AbstractTransactionalEntityService<CodeReview, CodeReviewDao> 
        implements CodeReviewService {

    private UserService userService;
    private PermissionService permissionService;
    
    /**
     * Create an instance of CodeReview entity based service
     * @param dao               data access object, which should be able do all CRUD operations with entity. 
     * @param userService       to get current user
     * @param permissionService to check permission for current user ({@link org.springframework.security.access.prepost.PreAuthorize} annotation emulation)
     */
    public TransactionalCodeReviewService(
                                    CodeReviewDao dao,
                                    UserService userService,
                                    PermissionService permissionService) {
        super(dao);
        this.userService = userService;
        this.permissionService = permissionService;
    }
    
    @Override
    public CodeReviewComment addComment(Long reviewId, int lineNumber, String body) 
            throws NotFoundException {
        CodeReview review = get(reviewId);
        JCUser currentUser = userService.getCurrentUser();
        
        permissionService.checkPermission(
                review.getTopic().getBranch().getId(), 
                AclClassName.BRANCH, 
                BranchPermission.LEAVE_COMMENTS_IN_CODE_REVIEW);
                
        CodeReviewComment comment = new CodeReviewComment();
        comment.setLineNumber(lineNumber);
        comment.setBody(body);
        comment.setCreationDate(new DateTime(System.currentTimeMillis()));
        comment.setAuthor(currentUser);
        
        review.addComment(comment);
        getDao().update(review);
        
        return comment;
    }
    
}
