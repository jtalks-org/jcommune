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
package org.jtalks.jcommune.service;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * The interface to manipulate with private messages.
 *
 * @author Pavel Vervenko
 * @author Kirill Afonin
 * @author Guram Savinov
 */
public interface PrivateMessageService extends EntityService<PrivateMessage> {

    /**
     * Get all inbox messages for the current user.
     *
     * @param page page number for needed messages.
     * @return object with messages for one page and pagination information.
     */
    Page<PrivateMessage> getInboxForCurrentUser(String page);

    /**
     * Get all outgoing messages from the current user.
     *
     * @param page page number for needed messages.
     * @return object with messages for one page and pagination information.
     *         Note that may be all messages for current user.
     */
    Page<PrivateMessage> getOutboxForCurrentUser(String page);

    /**
     * Send the private message to the user.
     *
     * @param title     the title of the message
     * @param body      the body of the message
     * @param recipient user of receiver
     * @param userFrom  user of sender
     * @return sent message
     * @throws NotFoundException if the receiver not exists
     */
    PrivateMessage sendMessage(String title, String body, JCUser recipient, JCUser userFrom) throws NotFoundException;

    /**
     * Get current user's drafts
     *
     * @param page page number for needed messages.
     * @return object with messages for one page and pagination information.
     *         Note that may be all messages for current user.
     */
    Page<PrivateMessage> getDraftsForCurrentUser(String page);

    /**
     * Save message as draft. If message exist it will be updated.
     *
     * @param id        message id.
     * @param userTo    receiver of the message
     * @param title     the title of the message.
     * @param body      the body of the message.
     * @param userFrom  sender.
     */
    void saveDraft(long id, JCUser userTo, String title, String body, JCUser userFrom);

    /**
     * Get count of new messages for current user.
     *
     * @return count of new messages
     */
    int currentUserNewPmCount();

    /**
     * Send draft message.
     * After sending message will given "unread" status.
     *
     * @param id        message id
     * @param title     the title of the message
     * @param body      the body of the message
     * @param recipient user of receiver
     * @param userFrom  user of sender
     * @return saved message
     * @throws NotFoundException if the receiver does not exist
     */
    PrivateMessage sendDraft(long id, String title, String body, JCUser recipient, JCUser userFrom)
            throws NotFoundException;

    /**
     * Delete or change status of messages by id.
     * For messages with SENT status this method change status to
     * DELETED_FROM_INBOX or DELETED_FROM_OUTBOX.
     * Messages with status DELETED_FROM_INBOX, DELETED_FROM_OUTBOX
     * or DRAFT will be removed.
     *
     * @param ids Identifiers of messages for deletion
     * @return URL for redirection.
     * @throws NotFoundException if one or more messages specified are missing
     */
    String delete(List<Long> ids) throws NotFoundException;

    /**
     * This methods checks a permissions of user to send
     * private message.
     *
     * @param senderId an identifier of sender of private message
     */
    void checkPermissionsToSend(Long senderId);
}
