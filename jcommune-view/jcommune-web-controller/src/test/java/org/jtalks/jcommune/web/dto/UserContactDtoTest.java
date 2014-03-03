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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class UserContactDtoTest {

	@Test
	public void testCreateFromUserContact() {
	    UserContact userContact = prepareUserContact();
		//create
		UserContactDto contactDto = new UserContactDto(userContact);
		//check content
		assertEquals(contactDto.getValue(), userContact.getValue(),
				"The problem of copying data. Value - value.");
		assertEquals(contactDto.getId(), Long.valueOf(userContact.getId()),
				"The problem of copying data. Value - Id.");
		assertEquals(contactDto.getType().getId(), userContact.getType().getId(),
				"The problem of copying data. Value - type.");
	}

    private UserContact prepareUserContact(){
        //prepare help test values
        UserContactType contactType = new UserContactType();
        JCUser user = new JCUser("username", "email", "password");
        user.setId(1);
        //init main test value
        UserContact userContact = new UserContact("10-10-10", contactType);
        userContact.setOwner(user);
        return userContact;
    }
}
