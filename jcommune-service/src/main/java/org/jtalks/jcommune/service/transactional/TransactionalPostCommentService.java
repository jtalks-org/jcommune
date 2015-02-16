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
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.service.PluginCommentService;
import org.jtalks.jcommune.service.PostCommentService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;

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
    @PreAuthorize("(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.EDIT_OWN_POSTS') and " +
            "#comment.author.username == principal.username) or " +
            "(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.EDIT_OTHERS_POSTS') and " +
            "#comment.author.username != principal.username)")
    public PostComment updateComment(long id, String body, long branchId) throws NotFoundException {

        PostComment comment = get(id);
        comment.setBody(body);
        getDao().saveOrUpdate(comment);

        return comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostComment getComment(long id) throws NotFoundException {
        return getDao().get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreAuthorize("(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OWN_POSTS') and " +
            "#comment.author.username == principal.username) or " +
            "(hasPermission(#post.topic.branch.id, 'BRANCH', 'BranchPermission.DELETE_OTHERS_POSTS') and " +
            "#comment.author.username != principal.username)")
    public void markCommentAsDeleted(Post post, PostComment comment) {
        comment.setDeleted(true);
        getDao().saveOrUpdate(comment);
    }
}
