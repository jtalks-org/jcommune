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

import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.jcommune.model.dao.BranchReadedMarkerDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.BranchReadedMarker;
import org.jtalks.jcommune.model.entity.JCUser;

/**
 * @author Mikhail Stryzhonok
 */
public class BranchReadedMarkerHibernateDao extends GenericDao<BranchReadedMarker> implements BranchReadedMarkerDao {
    /**
     * @param sessionFactory The SessionFactory.
     */
    public BranchReadedMarkerHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, BranchReadedMarker.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BranchReadedMarker markBranchAsRead(JCUser forWhom, Branch branch) {
        BranchReadedMarker marker = getMarkerFor(forWhom, branch);
        if (marker == null) {
            marker = new BranchReadedMarker(forWhom, branch);
        } else {
            marker.setMarkTime(new DateTime());
        }
        saveOrUpdate(marker);
        return marker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BranchReadedMarker getMarkerFor(JCUser user, Branch branch) {
        return (BranchReadedMarker) session().getNamedQuery("getMarkByUserAndBranch")
                .setParameter("user", user)
                .setParameter("branch", branch)
                .uniqueResult();
    }
}
