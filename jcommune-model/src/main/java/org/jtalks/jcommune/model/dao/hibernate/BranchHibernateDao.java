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
package org.jtalks.jcommune.model.dao.hibernate;

import org.jtalks.common.model.dao.hibernate.AbstractHibernateChildRepository;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.List;

/**
 * Hibernate DAO implementation for operations with a {@link Branch}.
 *
 * @author Vitaliy Kravchenko
 * @author Max Malakhov
 * @author Eugeny Batov
 * @author Anuar Nurmakanov
 * @author masyan
 */
public class BranchHibernateDao extends AbstractHibernateChildRepository<Branch> implements BranchDao {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Branch> getAllBranches() {
        List<Branch> branches = getSession()
                .createCriteria(Branch.class)
                .setCacheable(true)
                .list();
        return branches;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Branch> getBranchesInSection(Long sectionId) {
        List<Branch> branches = getSession()
                .createQuery("from org.jtalks.jcommune.model.entity.Branch b where b.section = ?")
                .setCacheable(true)
                .setLong(0, sectionId)
                .list();
        return branches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountPostsInBranch(Branch branch) {
        Number count = (Number) getSession()
                .getNamedQuery("getCountPostsInBranch")
                .setParameter("branch", branch)
                .uniqueResult();
        return count.intValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountUnreadPostsInBranch(Branch branch, JCUser user) {
        Number count = (Number) getSession()
                .getNamedQuery("getCountUnreadPostsInBranch")
                .setParameter("user", user.getId())
                .setParameter("branch", branch.getId())
                .uniqueResult();
        return count.intValue();
    }
}