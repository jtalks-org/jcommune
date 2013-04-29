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

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.service.BranchLastPostService;

/**
 * An implementation of {@link BranchLastPostService} that based
 * on working with database. It provides an ability to find the
 * last post in database for branch and save it in the branch field.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class TransactionalBranchLastPostService implements BranchLastPostService {
    private PostDao postDao;
    private BranchDao branchDao;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param postDao for getting the last post of the branch
     * @param branchDao it's needed to update branch
     */
    public TransactionalBranchLastPostService(PostDao postDao, BranchDao branchDao) {
        this.postDao = postDao;
        this.branchDao = branchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshLastPostInBranch(Branch branch) {
        Post lastPostOfBranch = postDao.getLastPostFor(branch);
        branch.setLastPost(lastPostOfBranch);
        branchDao.saveOrUpdate(branch);
    }
}
