/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */

package org.jtalks.jcommune.model.dao.hibernate;

import java.util.List;

import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.entity.Section;

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
        return getSession().createQuery("from Section s order by s.position asc")
                .setCacheable(true).list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(Long id) {
        //TODO: not efficient solution. See more info on the next link http://bit.ly/m85eLs
        Section section = get(id);
        if (section == null) {
            return false;
        }
        getSession().delete(section);
        return true;
    }

}
