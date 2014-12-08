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

import org.jtalks.common.model.dao.Crud;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.BranchReadedMarker;
import org.jtalks.jcommune.model.entity.JCUser;

/**
 * DAO for {@link BranchReadedMarker}
 *
 * @author Mikhail Stryzhonok
 * @see {@link org.jtalks.jcommune.model.dao.hibernate.BranchReadedMarkerHibernateDao}
 */
public interface BranchReadedMarkerDao extends Crud<BranchReadedMarker> {

    /**
     * Mark all topics as read for specified user
     *
     * @param forWhom user for which branch will be marked
     * @param branch branch contained topics to mark
     *
     * @return newly created marker
     */
    BranchReadedMarker markBranchAsRead(JCUser forWhom, Branch branch);

    /**
     * Gets marker for specified user on specified branch
     *
     * @param user user to find mark
     * @param branch intrested branch
     *
     * @return marker for specified user on specified branch
     *          or null if marker not exist
     */
    BranchReadedMarker getMarkerFor(JCUser user, Branch branch);
}
