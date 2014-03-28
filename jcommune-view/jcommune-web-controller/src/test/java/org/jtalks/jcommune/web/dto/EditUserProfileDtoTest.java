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
import org.jtalks.jcommune.model.entity.UserContactType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * @author Andrey Pogorelov
 */
public class EditUserProfileDtoTest {

    private EditUserProfileDto dto;

    @BeforeMethod
    public void setUp(){
        dto = new EditUserProfileDto(new JCUser("username", "email", "password"));
    }

    @Test
    public void getUserContactsShouldReturnEmptyListIfThereAreNoContacts() {
        dto.setUserContactsDto(null);

        assertEquals(dto.getUserContacts().size(), 0);
    }

    @Test
    public void getUserContactsShouldReturnContactsFromDto() {
        UserContactDto contact = new UserContactDto();
        contact.setId(1L);
        contact.setValue("value");
        contact.setType(new UserContactType());
        List<UserContactDto> contactList = new ArrayList<>();
        contactList.add(contact);

        UserContactsDto contactsDto = new UserContactsDto();
        contactsDto.setContacts(contactList);
        dto.setUserContactsDto(contactsDto);

        assertEquals(dto.getUserContacts().size(), 1);
    }

}
