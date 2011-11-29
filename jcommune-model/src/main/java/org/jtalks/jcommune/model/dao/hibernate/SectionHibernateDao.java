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

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Section;

import java.util.List;

/**
 * Hibernate DAO implementation from the {@link Section}.
 *
 * @author Max Malakhov
 */
public class SectionHibernateDao extends ParentRepositoryImpl<Section> implements SectionDao {


    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Section> getAll() {
        List<Section> sectionList = getSession().createQuery("from Section s order by s.position asc")
                .setCacheable(true).list();
        return sectionList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Long id) {
        //TODO: inefficient solution. See more info on the next link http://bit.ly/m85eLs
        Section section = get(id);
        if (section == null) {
            return false;
        }
        getSession().delete(section);
        return true;
    }

}
