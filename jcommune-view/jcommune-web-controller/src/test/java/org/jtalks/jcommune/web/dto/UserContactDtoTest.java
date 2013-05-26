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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class UserContactDtoTest {
	//help test values
	private static final String CONTACT_TEST_VALUE = "10-10-10";
	private static final Long USER_ID_TEST_VALUE = Long.valueOf(1);
    private static final Long CONTACT_ID_TEST_VALUE = Long.valueOf(2);
	private UserContactType contactType;
	private JCUser user;
	//main test value
	private UserContact userContact;
	
	@BeforeMethod
	public void init() {
		//prepare help test values
		contactType = new UserContactType();
		user = new JCUser("username", "email", "password");
		user.setId(USER_ID_TEST_VALUE);
		//init main test value
		userContact = new UserContact(CONTACT_TEST_VALUE, contactType);
		userContact.setOwner(user);
	}
	
	@Test
	public void testCreateFromUserContact() {
		//create 
		UserContactDto contactDto = new UserContactDto(userContact);
		//check content
		assertEquals(contactDto.getValue(), CONTACT_TEST_VALUE, 
				"The problem of copying data. Value - value.");
		assertEquals(contactDto.getOwnerId(), USER_ID_TEST_VALUE,
				"The problem of copying data. Value - ownerId.");
		assertEquals(contactDto.getTypeId(), contactType.getId(), 
				"The problem of copying data. Value - type.");
	}

    @Test
    public void testCreateContactWithSetters() {
        UserContactDto contactDto = new UserContactDto();
        contactDto.setValue(CONTACT_TEST_VALUE);
        contactDto.setOwnerId(USER_ID_TEST_VALUE);
        contactDto.setTypeId(contactType.getId());
        contactDto.setId(CONTACT_ID_TEST_VALUE);

        assertEquals(contactDto.getValue(), CONTACT_TEST_VALUE,
                "The problem of copying data. Value - value.");
        assertEquals(contactDto.getOwnerId(), USER_ID_TEST_VALUE,
                "The problem of copying data. Value - ownerId.");
        assertEquals(contactDto.getTypeId(), contactType.getId(),
                "The problem of copying data. Value - type.");
        assertEquals(contactDto.getId(), CONTACT_ID_TEST_VALUE,
                "The problem of copying data. Value - id.");
    }
}
