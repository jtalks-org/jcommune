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
import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.entity.JCUser;

import java.util.List;

/**
 * DAO for the {@link Section} objects.
 *
 * @author Max Malakhov
 * @see org.jtalks.jcommune.model.dao.hibernate.SectionHibernateDao
 */
public interface SectionDao extends Crud<Section> {

    /**
     * Get the list of all sections.
     *
     * @return list of section
     */
    List<Section> getAll();

    /**
     * Get count branches with VIEW_TOPICS permission
     * @param user current user
     * @param branches branches of current section
     * @return count branches
     */
    long getCountAvailableBranches(JCUser user, List<Branch> branches);

    List<Long> getAvailableBranchIds(JCUser user, List<Branch> branches);
}
