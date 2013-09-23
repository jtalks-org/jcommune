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
import org.jtalks.jcommune.model.dto.PageRequest;
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
    public static final int DEFAULT_MESSAGE_COUNT = 0;

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
    public Page<PrivateMessage> getAllFromUser(JCUser userFrom, PageRequest pageRequest) {
        PrivateMessageStatus[] statuses = PrivateMessageStatus.getOutboxStatus();
        int totalCount = getOutboxMessageCount(userFrom, statuses);
        pageRequest.adjustPageNumber(totalCount);

        Query query = session().getNamedQuery("getAllFromUser")
                .setParameterList(STATUSES, statuses)
                .setEntity("user", userFrom);

        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());

        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();

        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount);
    }

    /**
     * Return outbox message count for specified user (sender) and message statuses.
     * @param userFrom Specified user (sender) for witch will be returned outbox message count.
     * @param statuses The private message statuses for inbox page.
     * @return The inbox message count.
     */
    private int getOutboxMessageCount(JCUser userFrom, PrivateMessageStatus[] statuses) {
        int result = DEFAULT_MESSAGE_COUNT;
        Number messageCount = (Number) session()
                .getNamedQuery("getCountUserOutboxPm")
                .setParameter("userFrom", userFrom)
                .setParameterList(STATUSES, statuses)
                .uniqueResult();
        if (messageCount != null) {
            result = messageCount.intValue();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<PrivateMessage> getAllForUser(JCUser userTo, PageRequest pageRequest) {
        PrivateMessageStatus[] statuses = PrivateMessageStatus.getInboxStatus();
        int totalCount = getInboxMessageCount(userTo, statuses);
        pageRequest.adjustPageNumber(totalCount);
        Query query = session().getNamedQuery("getAllToUser")
                .setParameterList(STATUSES, statuses)
                .setEntity("user", userTo);
        query.setFirstResult(pageRequest.getOffset());
        query.setMaxResults(pageRequest.getPageSize());
        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();
        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount);
    }

    /**
     * Return inbox message count for specified user and message statuses.
     * @param user Specified user for witch will be returned inbox message count.
     * @param statuses The private message statuses for inbox page.
     * @return The inbox message count.
     */
    private int getInboxMessageCount(JCUser user, PrivateMessageStatus[] statuses) {
        int result = DEFAULT_MESSAGE_COUNT;
        Number messageCount = (Number) session()
                .getNamedQuery("getCountUserInboxPm")
                .setParameter("userTo", user)
                .setParameterList(STATUSES, statuses)
                .uniqueResult();
        if (messageCount != null) {
            result = messageCount.intValue();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Page<PrivateMessage> getDraftsForUser(JCUser user, PageRequest pageRequest) {
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
