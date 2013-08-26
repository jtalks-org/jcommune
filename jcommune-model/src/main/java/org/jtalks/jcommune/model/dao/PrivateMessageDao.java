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
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.springframework.data.domain.Page;

/**
 * DAO interface for private messaging. Except of basic CRUD operations from {@link Crud}
 * provides methods to get all messages from some user or to the user.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @see org.jtalks.jcommune.model.dao.hibernate.PrivateMessageHibernateDao
 */
public interface PrivateMessageDao extends Crud<PrivateMessage> {

    /**
     * Get all messages sent by specified user (either
     * {@link org.jtalks.jcommune.model.entity.PrivateMessageStatus#NEW}, or
     * {@link org.jtalks.jcommune.model.entity.PrivateMessageStatus#SENT} (Outbox page).
     *
     * @param userFrom    the sender
     * @param pageRequest pagination information.
     * @return {@link Page} with messages.
     */
    Page<PrivateMessage> getAllFromUser(JCUser userFrom, PageRequest pageRequest);

    /**
     * Get all private messages to the specified user (either
     * {@link org.jtalks.jcommune.model.entity.PrivateMessageStatus#NEW}, or
     * {@link org.jtalks.jcommune.model.entity.PrivateMessageStatus#SENT} (Inbox page).
     *
     * @param userTo the recipient of the messages
     * @param pageRequest pagination information.
     * @return {@link Page} with messages.
     */
    Page<PrivateMessage> getAllForUser(JCUser userTo, PageRequest pageRequest);

    /**
     * Get draft messages for user,(
     * {@link org.jtalks.jcommune.model.entity.PrivateMessageStatus#DRAFT} (Draft page).
     *
     * @param user drafts author
     * @param pageRequest pagination information.
     * @return {@link Page} with messages.
     */
    Page<PrivateMessage> getDraftsForUser(JCUser user, PageRequest pageRequest);

    /**
     * Get count of new (unread) messages for user.
     *
     * @param username username
     * @return count of new messages
     */
    int getNewMessagesCountFor(String username);
}
