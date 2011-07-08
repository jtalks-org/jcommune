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
package org.jtalks.jcommune.model.entity;

import org.joda.time.DateTime;

/**
 * Text message to send from one user to another. <br/>
 * All fields are required.
 * Use the static method PrivateMessage.createNewPrivateMessage()
 * to create new PrivateMessage with current creationDate.
 *
 * @author Pavel Vervenko
 */
public class PrivateMessage extends Persistent {

    private DateTime creationDate;
    private User userFrom;
    private User userTo;
    private String title;
    private String body;

    private PrivateMessageStatus status = PrivateMessageStatus.NOT_READED;

    /**
     * Creates the PrivateMessage instance. All fields values are null.
     */
    public PrivateMessage() {
    }

    /**
     * Creates the PrivateMessage with the specified creation date.
     *
     * @param creationDate the pm's creation date
     */
    public PrivateMessage(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Creates a new PrivateMessage with the creationDate initialized with current time.
     *
     * @return newly created PrivateMessage
     */
    public static PrivateMessage createNewPrivateMessage() {
        return new PrivateMessage(new DateTime());
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
    public void setCreationDate(DateTime creationDate) {
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
    public User getUserFrom() {
        return userFrom;
    }

    /**
     * Set the User who send the message.
     *
     * @param userFrom sender
     */
    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    /**
     * Get the recipient of the message.
     *
     * @return recipient
     */
    public User getUserTo() {
        return userTo;
    }

    /**
     * Set the recipient of the message.
     *
     * @param userTo recipient of the message
     */
    public void setUserTo(User userTo) {
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
     * Mark message as readed.
     */
    public void markAsReaded() {
        this.status = PrivateMessageStatus.READED;
    }

    /**
     * @return {@code true} if message is readed
     */
    public boolean isReaded() {
        return this.status == PrivateMessageStatus.READED;
    }
}
