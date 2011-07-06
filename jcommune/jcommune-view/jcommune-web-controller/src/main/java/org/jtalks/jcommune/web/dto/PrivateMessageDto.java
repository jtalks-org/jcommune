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
package org.jtalks.jcommune.web.dto;

import org.hibernate.validator.constraints.NotEmpty;
import org.jtalks.jcommune.model.entity.PrivateMessage;

import javax.validation.constraints.Size;

/**
 * DTO for {@link PrivateMessage} objects. Used for validation and binding to the form.
 * Holds message's title, body and username of the recipient.
 *
 * @author Pavel Vervenko
 * @author Alexandre Teterin
 */
public class PrivateMessageDto {

    /**
     * Message's title. Must be 2-22 chars
     */
    @Size(min = 2, max = 22, message = "{title.length}")
    private String title;
    @Size(min = 2, message = "{body.length}")
    private String body;
    @NotEmpty(message = "{not_empty}")
    private String recipient;

    /**
     * @return pm id
     */
    public long getId() {
        return id;
    }

    /**
     * Set pm id.
     *
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    private long id;

    /**
     * Get the text content of the message's body.
     *
     * @return message body
     */
    public String getBody() {
        return body;
    }

    /**
     * Set the message content.
     *
     * @param body message body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Get the username of message's receiver.
     *
     * @return recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Set the recipient's username.
     *
     * @param recipient recipient username
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Get the text title of the message.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the message title.
     *
     * @param title message's title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Create dto from {@link PrivateMessage}
     *
     * @param pm private message for conversion
     * @return dto for private message
     */
    public static PrivateMessageDto getDtoFor(PrivateMessage pm) {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setBody(pm.getBody());
        dto.setTitle(pm.getTitle());
        dto.setRecipient(pm.getUserTo().getUsername());
        dto.setId(pm.getId());
        return dto;
    }

    /**
     * Create reply dto from {@link PrivateMessage}
     * @param pm private message for conversion in to reply
     * @return dto for reply
     */
    public static PrivateMessageDto getDtoForReply(PrivateMessage pm) {
        PrivateMessageDto pmDto = new PrivateMessageDto();
        pmDto.setRecipient(pm.getUserFrom().getUsername());
        pmDto.setTitle(setTitleForReply(pm.getTitle()));
        return pmDto;
    }


    /**
     * Create quote dto from {@link PrivateMessage}
     * @param pm private message for conversion in to the quote
     * @return dto for quote
     */
    public static PrivateMessageDto getDtoForQuote (PrivateMessage pm) {
        PrivateMessageDto pmDto = getDtoForReply(pm);
        pmDto.setBody(setBodyForQuote(pm.getBody()));
        return pmDto;
    }

    /**
     * Create title for reply message
     * @param pmTitle original message title
     * @return reply title
     */
    public static String setTitleForReply(String pmTitle) {
        //check the "Re: " occurrence in the  title of original message and modifying it.
        return pmTitle.contains("Re: ") ? pmTitle : "Re: " + pmTitle;
    }

    /**
     * Create body for quote message
     * @param pmBody original message body
     * @return quote body
     */
    public static String setBodyForQuote(String pmBody) {
        return "\n" +"< " + pmBody;
    }
}
