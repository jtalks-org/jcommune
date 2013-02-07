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

import java.util.Collection;

import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
import org.jtalks.jcommune.model.dao.BannerDao;
import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.model.entity.BannerPosition;

/**
 * An implementation of {@link BannerDao} that is based on Hibernate and working
 * with database.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class BannerHibernateDao extends AbstractHibernateParentRepository<Banner> 
    implements BannerDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public Banner getByPosition(BannerPosition positionOnPage) {
        return (Banner) getSession()
                .getNamedQuery("getByPosition")
                .setParameter("position", positionOnPage)
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Banner> getAll() {
        return getSession()
                .getNamedQuery("getAll")
                .list();
    }
}
