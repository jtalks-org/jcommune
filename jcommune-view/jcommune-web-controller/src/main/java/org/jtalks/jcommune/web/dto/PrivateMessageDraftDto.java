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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.web.validation.annotations.AtLeastOneNotEmpty;
import org.jtalks.jcommune.web.validation.annotations.Exists;

import javax.validation.constraints.Size;

/**
 * This class has same fields as {@link org.jtalks.jcommune.web.dto.PrivateMessageDto} but different validation
 * rules. Needed to implement different validation rules while saving drafts
 *
 * @author Mikhail Stryzhonok
 */
@AtLeastOneNotEmpty(fieldNames = {"body", "title"})
public class PrivateMessageDraftDto {

    @Size(max = PrivateMessage.MAX_TITLE_LENGTH)
    private String title;

    @Size(max = PrivateMessage.MAX_MESSAGE_LENGTH)
    private String body;

    @Exists(entity = JCUser.class, field = "username", message = "{validation.wrong_recipient}", ignoreCase=true,
            isNullableAllowed = true)
    private String recipient;

    private long id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
