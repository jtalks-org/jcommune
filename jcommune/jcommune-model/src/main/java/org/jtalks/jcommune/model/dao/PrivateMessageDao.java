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
package org.jtalks.jcommune.model.dao;

import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;

import java.util.List;

/**
 * DAO interface for private messaging. Except of basic CRUD operations from {@link Dao}
 * provides methods to get all messages from some user or to the user.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @see org.jtalks.jcommune.model.dao.hibernate.PrivateMessageHibernateDao
 */
public interface PrivateMessageDao extends Dao<PrivateMessage> {

    /**
     * Get all messages sent by specified user.
     *
     * @param userFrom the sender
     * @return the list of messages
     */
    List<PrivateMessage> getAllFromUser(User userFrom);

    /**
     * Get all private messages to the specified user.
     *
     * @param userTo the recipient of the messages
     * @return the list of messages
     */
    List<PrivateMessage> getAllForUser(User userTo);

    /**
     * Get draft messages for user,
     *
     * @param user drafts author
     * @return list of draft messages
     */
    List<PrivateMessage> getDraftsFromUser(User user);
}
