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
package org.jtalks.jcommune.service;

import java.util.List;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.service.exceptions.NotFoundException;

/**
 * The interface to manipulate with private messages.
 * 
 * @author Pavel Vervenko
 */
public interface PrivateMessageService extends EntityService<PrivateMessage> {

    /**
     * Get all inbox messages for the current user.
     * @return the list of messages
     */
    List<PrivateMessage> getInboxForCurrentUser();

    /**
     * Get all outgoing messages from the current user.
     * @return the list of messages
     */
    List<PrivateMessage> getOutboxForCurrentUser();

    /**
     * Send the private message to the user.
     * @param title the title of the message
     * @param body the body of the message
     * @param recipient username of receiver
     * @return sent message
     * @throws NotFoundException if the receiver not exists
     */
    public PrivateMessage sendMessage(String title, String body, String recipient) throws NotFoundException;
}
