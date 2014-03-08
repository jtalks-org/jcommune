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
import org.jtalks.jcommune.model.entity.UserContact;
import org.jtalks.jcommune.model.entity.UserContactType;

import javax.validation.Valid;
import java.util.*;

/**
 * This dto used for transferring data in edit {@link org.jtalks.jcommune.model.entity.JCUser} operation.
 * To get more info see
 * {@link org.jtalks.jcommune.web.controller.UserProfileController#saveEditedContacts(EditUserProfileDto,
 * org.springframework.validation.BindingResult, javax.servlet.http.HttpServletResponse)}.
 *
 * @author Andrey Pogorelov
 */
public class UserContactsDto {

    @Valid
    List<UserContactDto> contacts;

    Map<Long, String> contactTypes;

    public UserContactsDto() {

    }

    public UserContactsDto(JCUser user) {
        contacts = new ArrayList<>();
        Set<UserContact> contactList = user.getUserContacts();
        for (UserContact contact : contactList) {
            contacts.add(new UserContactDto(contact));
        }
        Collections.sort(contacts);
    }

    /**
     * @return user contact dto's
     */
    public List<UserContactDto> getContacts() {
        return contacts;
    }

    /**
     * Set user contacts
     *
     * @param contacts user contacts
     */
    public void setContacts(List<UserContactDto> contacts) {
        this.contacts = contacts;
    }

    /**
     * @return user contact types as pairs contactTypeId - contactTypeName
     */
    public Map<Long, String> getContactTypes() {
        return contactTypes;
    }

    /**
     * Set user contact types
     *
     * @param contactTypes user contact types as pairs contactTypeId - contactTypeName
     */
    public void setContactTypes(Map<Long, String> contactTypes) {
        this.contactTypes = contactTypes;
    }

    public void setContactTypes(List<UserContactType> contactTypes) {
        this.contactTypes = new HashMap<>();
        for (UserContactType contactType : contactTypes) {
            this.contactTypes.put(contactType.getId(), contactType.getTypeName());
        }

    }
}
