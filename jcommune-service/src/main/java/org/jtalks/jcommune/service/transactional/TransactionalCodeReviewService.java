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
import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.entity.CodeReview;
import org.jtalks.jcommune.model.entity.CodeReviewComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.CodeReviewService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.NotificationService;
import org.jtalks.jcommune.service.security.AclClassName;
import org.jtalks.jcommune.service.security.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * The implementation of (@link {@link CodeReviewService}
 *
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalCodeReviewService extends AbstractTransactionalEntityService<CodeReview, Crud<CodeReview>>
        implements CodeReviewService {

    private UserService userService;
    private PermissionService permissionService;
    private NotificationService notificationService;

    /**
     * Create an instance of CodeReview entity based service
     *
     * @param dao                 data access object, which should be able do all CRUD operations with entity.
     * @param userService         to get current user
     * @param permissionService   to check permission for current user ({@link org.springframework.security.access
     *                            .prepost.PreAuthorize}
     *                            annotation emulation)
     * @param notificationService to send email updates for comment adding subscribers
     */
    public TransactionalCodeReviewService(
            Crud<CodeReview> dao,
            UserService userService,
            PermissionService permissionService,
            NotificationService notificationService) {
        super(dao);
        this.userService = userService;
        this.permissionService = permissionService;
        this.notificationService = notificationService;
    }

    @Override
    public CodeReviewComment addComment(Long reviewId, int lineNumber, String body) throws NotFoundException {
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
        if (currentUser.isAutosubscribe()) {
            review.getSubscribers().add(currentUser);
        }

        review.addComment(comment);
        getDao().saveOrUpdate(review);
        notificationService.subscribedEntityChanged(review);

        return comment;
    }

    /**
     * Checks permissions for deletion user posts and review comments and delete comment with defined ID.
     *
     * @param reviewComment ID of code review comment
     * @param codeReview    ID of code review where needs to delete comment
     */
    @PreAuthorize("(hasPermission(#codeReview.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS') and " +
            "#reviewComment.author.username == principal.username) or " +
            "(hasPermission(#codeReview.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OTHERS_POSTS') and " +
            "#reviewComment.author.username != principal.username)")
    public void deleteComment(CodeReviewComment reviewComment, CodeReview codeReview) {
        codeReview.getComments().remove(reviewComment);
        getDao().saveOrUpdate(codeReview);
    }

}
