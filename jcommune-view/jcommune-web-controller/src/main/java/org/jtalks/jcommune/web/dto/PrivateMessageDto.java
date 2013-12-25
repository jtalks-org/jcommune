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
package org.jtalks.jcommune.web.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;
import org.jtalks.jcommune.web.validation.annotations.BbCodeNesting;
import org.jtalks.jcommune.web.validation.annotations.Exists;
import org.jtalks.jcommune.web.validation.annotations.NotMe;

import javax.validation.constraints.Size;

/**
 * DTO for {@link PrivateMessage} objects. Used for validation and binding to the form.
 * Holds message's title, body and username of the recipient.
 * <p/>
 * Validation is not applied when saving message as a draft
 *
 * @author Pavel Vervenko
 * @author Alexandre Teterin
 */
public class PrivateMessageDto {


    @NotBlank
    @Size(min = PrivateMessage.MIN_TITLE_LENGTH, max = PrivateMessage.MAX_TITLE_LENGTH, message = "{title.length}")
    private String title;

    @NotBlank
    @BbCodeAwareSize(min = PrivateMessage.MIN_MESSAGE_LENGTH,
            max = PrivateMessage.MAX_MESSAGE_LENGTH, message = "{body.length}")
    @BbCodeNesting
    private String body;

    @NotMe(message = "{validation.username.notMe}")
    @Exists(entity = JCUser.class, field = "username", message = "{validation.wrong_recipient}", ignoreCase=true)
    private String recipient;

    private long id;

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
     * Create the full private message dto from {@link PrivateMessage}
     *
     * @param pm private message for conversion
     * @return dto for full private message
     */
    public static PrivateMessageDto getFullPmDtoFor(PrivateMessage pm) {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setBody(pm.getBody());
        dto.setTitle(pm.getTitle());
        if (pm.getUserTo() != null) {
            dto.setRecipient(pm.getUserTo().getUsername());
        }
        dto.setId(pm.getId());
        return dto;
    }

    /**
     * Create the reply private message dto from {@link PrivateMessage}
     * @param pm private message for conversion in to reply
     * @return dto for reply
     */
    public static PrivateMessageDto getReplyDtoFor(PrivateMessage pm) {
        PrivateMessageDto dto = new PrivateMessageDto();
        dto.setRecipient(pm.getUserFrom().getUsername());
        dto.setTitle(pm.prepareTitleForReply());
        return dto;
    }
}
