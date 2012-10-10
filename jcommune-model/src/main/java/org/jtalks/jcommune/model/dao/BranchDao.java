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
package org.jtalks.jcommune.model.dao;

import org.jtalks.common.model.dao.ChildRepository;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.List;

/**
 * DAO for the {@link Branch} objects.
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 * @author Evgeniy Naumenko
 * @author Eugeny Batov
 * @author masyan
 * @see org.jtalks.jcommune.model.dao.hibernate.BranchHibernateDao
 */

public interface BranchDao extends ChildRepository<Branch> {

    /**
     * Get all existing branches.
     *
     * @return list of {@code Branch} objects
     */
    List<Branch> getAllBranches();

    /**
     * Get branches from section.
     *
     * @param sectionId section id from which we obtain branches
     * @return list of {@code Branch} objects
     */
    List<Branch> getBranchesInSection(Long sectionId);

    /**
     * Get count of posts in the branch.
     *
     * @param branch the branch
     * @return count of posts in the branch
     */
    int getCountPostsInBranch(Branch branch);

    /**
     * Get state of unread posts in the branch.
     *
     * @param branch the branch
     * @param user   the user
     * @return state of unread posts in the branch for user
     */
    boolean isUnreadPostsInBranch(Branch branch, JCUser user);
}