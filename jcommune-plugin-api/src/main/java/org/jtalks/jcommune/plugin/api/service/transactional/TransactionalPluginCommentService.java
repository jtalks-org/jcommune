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
package org.jtalks.jcommune.plugin.api.service.transactional;

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.PostComment;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.PluginCommentService;

/**
 * Class for manipulating with comments from plugin.To manipulate with comments from jcommune use classes from service
 * module
 *
 * This class is  singleton because we can't use spring dependency injection mechanism in plugins due plugins can be
 * added or removed in runtime.
 *
 * @author Mikhail Stryzhonok
 */
public class TransactionalPluginCommentService implements PluginCommentService{
    private static final TransactionalPluginCommentService INSTANCE = new TransactionalPluginCommentService();

    private PluginCommentService commentService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private TransactionalPluginCommentService() {

    }

    /**
     * Gets instance of {@link TransactionalPluginCommentService class
     *
     * @return instance of {@link TransactionalPluginCommentService class
     */
    public static PluginCommentService getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PostComment updateComment(long id, String body, long branchId) throws NotFoundException {
        return commentService.updateComment(id, body, branchId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteComment(Post post, PostComment comment) {
        commentService.deleteComment(post, comment);
    }

    /**
     * Sets specified {@link org.jtalks.jcommune.plugin.api.service.PluginCommentService} implementation
     * Should be used once, during initialization
     *
     * @param commentService {@link org.jtalks.jcommune.plugin.api.service.PluginCommentService} implementation to set
     */
    public void setCommentService(PluginCommentService commentService) {
        this.commentService = commentService;
    }
}
