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
import org.jtalks.jcommune.model.dao.UserContactsDao;
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;

import java.util.List;

/**
 * This dao manages user ccontact and user contact types.
 *
 * Types are to be configured in Poulpe, so we need to retrieve them from a database.
 *
 * @author Evgeniy Naumenko
 */
public class UserContactsHibernateDao extends AbstractHibernateChildRepository<UserContactType>
        implements UserContactsDao {


    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserContactType> getAvailableContactTypes() {
        return getSession()
                .createQuery("from UserContactType")
                .setCacheable(true)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserContact getContactById(long id) {
        return (UserContact) getSession()
                .createQuery("from UserContact u where u.id = ?")
                .setCacheable(true)
                .setLong(0, id)
                .uniqueResult();
    }
}
