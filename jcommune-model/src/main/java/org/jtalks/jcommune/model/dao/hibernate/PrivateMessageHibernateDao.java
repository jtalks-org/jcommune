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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jtalks.common.model.dao.hibernate.GenericDao;
import org.jtalks.jcommune.model.dao.PrivateMessageDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.PrivateMessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * Hibernate implementation of PrivateMessageDao
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Guram Savinov
 */
public class PrivateMessageHibernateDao extends GenericDao<PrivateMessage> implements PrivateMessageDao {

    private static final String STATUS = "status";
    private static final String STATUSES = "statuses";

    /**
     * @param sessionFactory The SessionFactory.
     */
    public PrivateMessageHibernateDao(SessionFactory sessionFactory) {
        super(sessionFactory, PrivateMessage.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<PrivateMessage> getAllFromUser(JCUser userFrom, JCommunePageRequest pageRequest) {
        PrivateMessageStatus[] statuses = PrivateMessageStatus.getOutboxStatus();
        Number totalCount = (Number) session()
                .getNamedQuery("getCountUserSentPm")
                .setParameter("userFrom", userFrom)
                .setParameterList(STATUSES, statuses)
                .uniqueResult();
        pageRequest.adjustPageNumber(totalCount.intValue());

        Query query = session().getNamedQuery("getAllFromUser")
                .setParameterList(STATUSES, statuses)
                .setEntity("user", userFrom);

        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());

        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();

        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<PrivateMessage> getAllForUser(JCUser userTo, JCommunePageRequest pageRequest) {
        PrivateMessageStatus[] statuses = PrivateMessageStatus.getInboxStatus();
        Number totalCount = (Number) session()
                .getNamedQuery("getCountUserInboxPm")
                .setParameter("userTo", userTo)
                .setParameterList(STATUSES, statuses)
                .uniqueResult();
        pageRequest.adjustPageNumber(totalCount.intValue());
        Query query = session().getNamedQuery("getAllToUser")
                .setParameterList(STATUSES, statuses)
                .setEntity("user", userTo);
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();
        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<PrivateMessage> getDraftsForUser(JCUser user, JCommunePageRequest pageRequest) {
        Number totalCount = (Number) session()
                .getNamedQuery("getCountUserDraftPm")
                .setParameter("userFrom", user)
                .setParameter(STATUS, PrivateMessageStatus.DRAFT)
                .uniqueResult();
        pageRequest.adjustPageNumber(totalCount.intValue());
        Query query = session().getNamedQuery("getDraftsFromUser")
                .setParameter(STATUS, PrivateMessageStatus.DRAFT)
                .setParameter("user", user);
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();
        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNewMessagesCountFor(String username) {
        PrivateMessageStatus[] statuses = PrivateMessageStatus.getNewMessageStatus();
        return ((Number) session().getNamedQuery("getNewMessagesCountFor")
                .setParameter("read", false)
                .setString("username", username)
                .setParameterList(STATUSES, statuses)
                .uniqueResult())
                .intValue();
    }
}
