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
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.jcommune.model.dao.SimplePageDao;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.springframework.security.acls.model.NotFoundException;


/**
 * The implementation of {@link org.jtalks.jcommune.model.dao.SimplePageDao} based on Hibernate.
 * The class is responsible for loading {@link org.jtalks.jcommune.model.entity.SimplePage} objects from database,
 * save, update and delete them.
 *
 * @author Scherbakov Roman
 * @author Alexander Gavrikov
 */
public class SimplePageHibernateDao extends GenericDao<SimplePage> implements SimplePageDao {

    public SimplePageHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, SimplePage.class);
    }

    /**
     * {@inheritDoc}
     */
    public void createPage(SimplePage simplePage) {
        session().saveOrUpdate(simplePage);
    }

    /**
     * {@inheritDoc}
     */
    public SimplePage getPageByPathName(String pathName) throws NotFoundException {
        return (SimplePage) (session().getNamedQuery("getPageByPathName").
                setCacheable(true).setString("pathName", pathName).
                uniqueResult());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExist(String pagePathName) {
        return getPageByPathName(pagePathName) != null;
    }

}
