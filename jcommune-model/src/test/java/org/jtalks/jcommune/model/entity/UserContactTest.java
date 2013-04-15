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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class UserContactTest {

	private final static String DISPLAY_PATTERN = "aaa" + UserContactType.CONTACT_MASK_PLACEHOLDER + "bbb";
	
	
	private UserContact userContact;
	
	
	@BeforeMethod
	public void init() {
		userContact = new UserContact();
		userContact.setValue("111");
		
		UserContactType type = ObjectsFactory.getDefaultUserContactType();
		userContact.setType(type);
	}
	
	@Test
	public void testGetActualValue() {
		userContact.getType().setDisplayPattern(DISPLAY_PATTERN);
		
		Assert.assertEquals(userContact.getDisplayValue(), "aaa111bbb");
	}
	
	@Test
	public void testGetActualValueNullValue() {
		userContact.getType().setDisplayPattern(DISPLAY_PATTERN);
		userContact.setValue(null);
		
		Assert.assertEquals(userContact.getDisplayValue(), "aaabbb");
	}
	
	@Test(expectedExceptions=NullPointerException.class)
	public void testGetActualValueNullPattern() {
		userContact.getType().setDisplayPattern(null);
		
		Assert.assertEquals(userContact.getDisplayValue(), "aaabbb");
	}
}
