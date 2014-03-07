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

import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Entity;

/**
 * Text message sent from one user to another. <br/>
 * All fields are required.
 *
 * @author Pavel Vervenko
 * @author Alexandre Teterin
 */
public class PrivateMessage extends Entity {

    public static final int MAX_MESSAGE_LENGTH = 20000;
    public static final int MIN_MESSAGE_LENGTH = 2;
    public static final int MAX_TITLE_LENGTH = 120;
    public static final int MIN_TITLE_LENGTH = 2;

    private DateTime creationDate;
    private JCUser userFrom;
    private JCUser userTo;
    private String title;
    private String body;
    private boolean read;

    private PrivateMessageStatus status = PrivateMessageStatus.NEW;

    /**
     * For Hibernate use only
     */
    protected PrivateMessage() {
    }

    /**
     * Constructor with required parameters.
     *
     * @param userTo   recipient
     * @param userFrom sender
     * @param title    message title
     * @param body     message content
     */
    public PrivateMessage(JCUser userTo, JCUser userFrom, String title, String body) {
        this.creationDate = new DateTime();
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.title = title;
        this.body = body;
    }

    /**
     * Get the content of the message.
     *
     * @return message's body
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the message's body.
     *
     * @param body content to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get the creation timestamp of the message.
     *
     * @return creation date
     */
    public DateTime getCreationDate() {
        return creationDate;
    }

    /**
     * Set the creation date and time of the message.
     *
     * @param creationDate datetime to set
     */
    protected void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Get the title of the private message.
     *
     * @return message's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title to the message.
     *
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the User who send the message.
     *
     * @return sender
     */
    public JCUser getUserFrom() {
        return userFrom;
    }

    /**
     * Set the User who send the message.
     *
     * @param userFrom sender
     */
    protected void setUserFrom(JCUser userFrom) {
        this.userFrom = userFrom;
    }

    /**
     * Get the recipient of the message.
     *
     * @return recipient
     */
    public JCUser getUserTo() {
        return userTo;
    }

    /**
     * Set the recipient of the message.
     *
     * @param userTo recipient of the message
     */
    public void setUserTo(JCUser userTo) {
        this.userTo = userTo;
    }

    /**
     * Get message status
     *
     * @return message status
     * @see PrivateMessageStatus
     */
    public PrivateMessageStatus getStatus() {
        return status;
    }

    /**
     * Set message status.
     *
     * @param status message status
     * @see PrivateMessageStatus
     */
    public void setStatus(PrivateMessageStatus status) {
        this.status = status;
    }

    /**
     * @param read message read status
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * @return {@code true} if message is read
     */
    public boolean isRead() {
        return read;
    }


    /**
     * Prepare title for reply message
     *
     * @return reply title
     */
    public String prepareTitleForReply() {
        //check the "Re: " occurrence in the title of original message and modifying it.
        return title.startsWith("Re: ") ? getTitle() : "Re: " + getTitle();
    }

    /**
     * @return true if message reply is possible
     */
    public boolean isReplyAllowed(){
        return status.equals(PrivateMessageStatus.SENT)
                || status.equals(PrivateMessageStatus.DELETED_FROM_OUTBOX);
    }

}