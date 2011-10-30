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

import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.UserService;

import java.util.List;

/**
 * The implementation of SectionService
 *
 * @author Max Malakhov
 */

public class TransactionalSectionService extends AbstractTransactionalEntityService<Section, SectionDao>
        implements SectionService {

    private PostService postService;
    private UserService userService;

    /**
     * Create an instance of entity based service
     *
     * @param dao         data access object, which should be able do all CRUD operations.
     * @param postService for post info getting
     * @param userService for user info getting
     */
    public TransactionalSectionService(SectionDao dao, PostService postService, UserService userService) {
        super(dao);
        this.postService = postService;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Section> getAll() {
        return this.getDao().getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTopicInBranchCount(Branch branch) {
        return this.getDao().getTopicInBranchCount(branch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPostsOnForumCount() {
        return this.postService.getPostsOnForumCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getUsersCount() {
        return this.userService.getUsersCount();
    }
}
