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
import org.jtalks.common.model.dao.hibernate.AbstractHibernateParentRepository;
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
public class PrivateMessageHibernateDao extends
        AbstractHibernateParentRepository<PrivateMessage> implements PrivateMessageDao {

    private static final String STATUS = "status";
    private static final String STATUSES = "statuses";

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PrivateMessage> getAllFromUser(JCUser userFrom, JCommunePageRequest pageRequest) {
        PrivateMessageStatus[] notInStatuses = {PrivateMessageStatus.DRAFT, PrivateMessageStatus.DELETED_FROM_OUTBOX,
        PrivateMessageStatus.DELETED_FROM_INBOX};
        Number totalCount = (Number) getSession()
                .getNamedQuery("getCountUserSentPm")
                .setParameter("userFrom", userFrom)
                .setParameterList(STATUSES, notInStatuses)
                .uniqueResult();

        Query query = getSession().getNamedQuery("getAllFromUser")
                .setCacheable(true)
                .setParameterList(STATUSES, notInStatuses)
                .setEntity("user", userFrom);

        if (pageRequest.isPagingEnabled()) {
            query.setFirstResult(pageRequest.getIndexOfFirstItem());
            query.setMaxResults(pageRequest.getPageSize());
        }

        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();

        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<PrivateMessage> getAllForUser(JCUser userTo, JCommunePageRequest pageRequest) {
        PrivateMessageStatus[] notInStatuses = {PrivateMessageStatus.DRAFT, PrivateMessageStatus.DELETED_FROM_OUTBOX,
                PrivateMessageStatus.DELETED_FROM_INBOX};
        Number totalCount = (Number) getSession()
                .getNamedQuery("getCountUserInboxPm")
                .setParameter("userTo", userTo)
                .setParameterList(STATUSES, notInStatuses)
                .uniqueResult();
        Query query = getSession().getNamedQuery("getAllToUser")
                .setCacheable(true)
                .setParameterList(STATUSES, notInStatuses)
                .setEntity("user", userTo);
        if (pageRequest.isPagingEnabled()) {
            query.setFirstResult(pageRequest.getIndexOfFirstItem());
            query.setMaxResults(pageRequest.getPageSize());
        }
        List<PrivateMessage> messages = (List<PrivateMessage>) query.list();
        return new PageImpl<PrivateMessage>(messages, pageRequest, totalCount.intValue());
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
