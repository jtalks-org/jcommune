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

import org.jtalks.jcommune.model.entity.UserContactType;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

/**
 * 
 * @author Vyacheslav Mishcheryakov
 * 
 */
public class UserContactTypeTest {
	//help test values
	private static final String CONTACT_TEST_VALUE = "10-10-10";
	private static final String CONTACT_TEST_DISPLAY_PATTERN= "<a href='mailto:" + UserContactType.CONTACT_MASK_PLACEHOLDER + "'>" + UserContactType.CONTACT_MASK_PLACEHOLDER+"</a>";
	private static final String CONTACT_TEST_DISPLAY_VALUE = CONTACT_TEST_DISPLAY_PATTERN.replaceAll(UserContactType.CONTACT_MASK_PLACEHOLDER, CONTACT_TEST_VALUE);
	
	private UserContactType contactType;

	
	@BeforeMethod
	public void init() {
		//prepare help test values
		contactType = new UserContactType();
		contactType.setDisplayPattern(CONTACT_TEST_DISPLAY_PATTERN);
	}
	
	@Test
	public void testCreateFromUserContact() {
		//check content
		assertEquals(contactType.getDisplayValue(CONTACT_TEST_VALUE), 
				CONTACT_TEST_DISPLAY_VALUE, 
				"Wrong getDisplayValue method.");
	}
}
