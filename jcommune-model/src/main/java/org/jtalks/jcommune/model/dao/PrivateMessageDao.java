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

import org.jtalks.common.model.dao.ParentRepository;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;

import java.util.List;

/**
 * DAO interface for private messaging. Except of basic CRUD operations from {@link ParentRepository}
 * provides methods to get all messages from some user or to the user.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @see org.jtalks.jcommune.model.dao.hibernate.PrivateMessageHibernateDao
 */
public interface PrivateMessageDao extends ParentRepository<PrivateMessage> {

    /**
     * Get all messages sent by specified user.
     *
     * @param userFrom the sender
     * @return the list of messages
     */
    List<PrivateMessage> getAllFromUser(JCUser userFrom);

    /**
     * Get all private messages to the specified user.
     *
     * @param userTo the recipient of the messages
     * @return the list of messages
     */
    List<PrivateMessage> getAllForUser(JCUser userTo);

    /**
     * Get draft messages for user,
     *
     * @param user drafts author
     * @return list of draft messages
     */
    List<PrivateMessage> getDraftsFromUser(JCUser user);

    /**
     * Get count of new (unread) messages for user.
     *
     * @param username username
     * @return count of new messages
     */
    int getNewMessagesCountFor(String username);
}
