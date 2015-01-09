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
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.service.PluginPostService;

/**
 * @author Andrei Alikov
 */
public class TransactionalPluginPostService implements PluginPostService {

    private static final TransactionalPluginPostService INSTANCE = new TransactionalPluginPostService();

    private PluginPostService postService;

    /** Use {@link #getInstance()}, this class is singleton. */
    private TransactionalPluginPostService() {
    }

    /**
     * Gets instance of {@link TransactionalPluginPostService}
     *
     * @return instance of {@link TransactionalPluginPostService}
     */
    public static TransactionalPluginPostService getInstance() {
        return INSTANCE;
    }

    @Override
    public Post get(Long id) throws NotFoundException {
        return postService.get(id);
    }

    @Override
    public void deletePost(Post post) {
        postService.deletePost(post);
    }

    @Override
    public void updatePost(Post post, String postContent) throws NotFoundException {
        postService.updatePost(post, postContent);
    }

    /**
     * Sets post service. Should be used once, during initialization
     *
     * @param postService
     */
    public void setPostService(PluginPostService postService) {
        this.postService = postService;
    }
}
