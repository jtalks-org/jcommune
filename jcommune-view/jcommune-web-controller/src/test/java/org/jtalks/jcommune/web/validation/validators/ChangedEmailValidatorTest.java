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
package org.jtalks.jcommune.web.validation.validators;

import javax.validation.ConstraintValidatorContext;

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.validation.validators.ChangedEmailValidator;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class ChangedEmailValidatorTest {
	@Mock
	private SecurityService securityService;
	@Mock
	private UserDao userDao;
	@Mock
	private ConstraintValidatorContext validatorContext;
	private ChangedEmailValidator validator;
	private String userEmail = "current_user@gmail.com";
	private JCUser user = new JCUser("username", userEmail, "password");

	@BeforeMethod
	public void init() {
		MockitoAnnotations.initMocks(this);
		//
		Mockito.when(securityService.getCurrentUser()).thenReturn(user);
		//
		validator = new ChangedEmailValidator(securityService, userDao);
	}

	@Test
	public void testCheckUsingNullValue() {
		String value = null;
		boolean isValid = validator.isValid(value, validatorContext);
		Assert.assertEquals(isValid, true, "Null value isn't valid.");
	}

	@Test
	public void testUserEmailNotChanged() {
		boolean isValid = validator.isValid(userEmail, validatorContext);
		Assert.assertEquals(isValid, true, "Email of current user isn't valid.");
	}
	
	@Test
	public void testUserEmailChanged() {
		String value = "new_current_user@gmail.com";
		boolean isValid = validator.isValid(value, validatorContext);
		Assert.assertEquals(isValid, true, "New email isn't busy, but he invalid.");
		//
		Mockito.when(userDao.getByEmail(Mockito.anyString())).thenReturn(
				new JCUser("username", "email", "password"));
		isValid = validator.isValid(value, validatorContext);
		Assert.assertEquals(isValid, false, "New email is busy, but he valid.");
	}
}
