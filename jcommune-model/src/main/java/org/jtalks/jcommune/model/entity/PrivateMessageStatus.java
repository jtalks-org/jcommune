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
package org.jtalks.jcommune.model.entity;

/**
 * Private message status.
 *
 * If the message has been deleted from both Inbox and Outbox folders,
 * or has been deleted from Drafts no status should be set - message
 * is simply to be removed from a database
 *
 * @author Kirill Afonin
 * @author Evgeniy Naumenko
 */
public enum PrivateMessageStatus {
    /**
     * New message, has never be persisted in a database
     */
    NEW,
    /**
     * Saved as draft to be edited before sending
     */
    DRAFT,
    /**
     * Sent to the recipient.
     */
    SENT,
    /**
     * Recipient deleted this message from inbox folder
     */
    DELETED_FROM_INBOX,
    /**
     * Author deleted this message from outbox folder
     */
    DELETED_FROM_OUTBOX;

    /**
     * Return private message statuses for inbox page.
     * @return private message statuses for inbox page.
     */
    public static PrivateMessageStatus[] getInboxStatus() {
        return new PrivateMessageStatus[] {NEW, SENT, DELETED_FROM_OUTBOX};
    }

    /**
     * Return private message statuses for outbox page.
     * @return private message statuses for outbox page.
     */
    public static PrivateMessageStatus[] getOutboxStatus() {
        return new PrivateMessageStatus[] {NEW, SENT, DELETED_FROM_INBOX};
    }

    /**
     * Return private message statuses for new messages counter.
     * @return private message statuses for new messages counter.
     */
    public static PrivateMessageStatus[] getNewMessageStatus() {
        return new PrivateMessageStatus[] {SENT, DELETED_FROM_OUTBOX};
    }
}
