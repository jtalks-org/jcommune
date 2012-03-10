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

import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;

import java.util.List;

/**
 * Hibernate implementation of PrivateMessageDao
 * 
 * @author Pavel Vervenko
 * @author Kirill Afonin
 */
public class PrivateMessageHibernateDao extends
        AbstractHibernateParentRepository<PrivateMessage> implements PrivateMessageDao {

    private static final String STATUS = "status";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getAllFromUser(JCUser userFrom) {
        return getSession().getNamedQuery("getAllFromUser")
                .setCacheable(true)
                .setParameter(STATUS, PrivateMessageStatus.DRAFT)
                .setEntity("user", userFrom)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getAllForUser(JCUser userTo) {
        return getSession().getNamedQuery("getAllToUser")
                .setCacheable(true)
                .setParameter(STATUS, PrivateMessageStatus.DRAFT)
                .setEntity("user", userTo)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PrivateMessage> getDraftsFromUser(JCUser userFrom) {
        return getSession().getNamedQuery("getDraftsFromUser")
                .setCacheable(true)
                .setParameter(STATUS, PrivateMessageStatus.DRAFT)
                .setEntity("user", userFrom)
                .list();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNewMessagesCountFor(String username) {
        return ((Number) getSession().getNamedQuery("getNewMessagesCountFor")
                .setCacheable(true)
                .setParameter("read", false)
                .setString("username", username)
                .setParameter(STATUS, PrivateMessageStatus.SENT)
                .uniqueResult())
                .intValue();
    }
}
