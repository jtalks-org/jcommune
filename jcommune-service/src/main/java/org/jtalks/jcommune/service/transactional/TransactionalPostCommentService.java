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

import org.jtalks.common.model.dao.Crud;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.plugin.api.service.PluginCommentService;
import org.jtalks.jcommune.service.PostCommentService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.PermissionService;
import org.springframework.security.access.AccessDeniedException;

/**
 * The implementation of {@link org.jtalks.jcommune.service.PostCommentService}
 * 
 * @author Vyacheslav Mishcheryakov
 */
public class TransactionalPostCommentService extends
        AbstractTransactionalEntityService<PostComment, Crud<PostComment>> implements
        PostCommentService, PluginCommentService {

    private PermissionService permissionService;
    private UserService userService;

    /**
     * Create an instance of CodeReview entity based service
     * 
     * @param dao data access object, which should be able do all CRUD operations with entity.
     * @param permissionService to check permissions for actions
     * @param userService to get current user
     */
    public TransactionalPostCommentService(Crud<PostComment> dao,
                                           PermissionService permissionService, UserService userService) {
        super(dao);
        this.permissionService = permissionService;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostComment updateComment(long id, String body, long branchId) throws NotFoundException {

        PostComment comment = get(id);
        checkHasUpdatePermission(comment, branchId);

        comment.setBody(body);
        getDao().saveOrUpdate(comment);

        return comment;
    }

    /**
     * Checks if current user can edit review comments
     * 
     * @param comment
     *            - comment to check permissions on
     * @param branchId
     *            - ID of branch where review with comment located
     */
    private void checkHasUpdatePermission(PostComment comment, long branchId) {
        JCUser currentUser = userService.getCurrentUser();
        boolean canEditOwnPosts = permissionService.hasBranchPermission(branchId, BranchPermission.EDIT_OWN_POSTS);
        boolean canEditOthersPosts = permissionService
                .hasBranchPermission(branchId, BranchPermission.EDIT_OTHERS_POSTS);

        if (!(canEditOthersPosts && !comment.isCreatedBy(currentUser))
                && !(canEditOwnPosts && comment.isCreatedBy(currentUser))) {
            throw new AccessDeniedException("No permission to edit review comment");
        }
    }
}
