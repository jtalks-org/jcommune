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

import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.UserProfileDto;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * 
 * @author Anuar_Nurmakanov
 * 
 */
public class ChangedEmailValidatorTest {
	@Mock
	private UserService userService;
	@Mock
	private UserDao userDao;
	@Mock
	private ConstraintValidatorContext validatorContext;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext nodeBuilderDefinedContext;

	private ChangedEmailValidator validator;
	private String userEmail = "current_user@gmail.com";
	private JCUser user = new JCUser("username", userEmail, "password");

	@BeforeMethod
	public void init() throws NotFoundException {
		initMocks(this);
		when(userService.get(anyLong())).thenReturn(user);
		validator = new ChangedEmailValidator(userService, userDao);
	}

	@Test
	public void emptyEmailShouldBeValidBecauseItIsNotBusy() {
		UserProfileDto editedUserProfile = new UserProfileDto();
		editedUserProfile.setEmail(null);
        boolean isValid = validator.isValid(editedUserProfile, validatorContext);

        assertTrue(isValid, "Empty email should be valid, becuase it isn't busy.");
	}

	@Test
	public void notChangedEmailShouldBeValid() {
        UserProfileDto editedUserProfile = new UserProfileDto();
        editedUserProfile.setEmail(userEmail);

		boolean isValid = validator.isValid(editedUserProfile, validatorContext);

		assertTrue(isValid, "Email of current user isn't valid.");
	}

	@Test
	public void changedEmailShouldBeValidIfItIsNotBusy() {
        UserProfileDto editedUserProfile = new UserProfileDto();
        editedUserProfile.setEmail("new_current_user@gmail.com");

	    boolean isValid = validator.isValid(editedUserProfile, validatorContext);

	    assertTrue(isValid, "New email isn't taken, so it must be valid.");
	}

	@Test
    public void changedEmailShouldNotBeValidIfItIsBusy() {
        when(userDao.getByEmail(anyString())).thenReturn(
                new JCUser("new_current_user@gmail.com", "email", "password"));
        UserProfileDto editedUserProfile = new UserProfileDto();
        editedUserProfile.setEmail("new_current_user@gmail.com");
        when(validatorContext.buildConstraintViolationWithTemplate(null)).
                thenReturn(violationBuilder);
        when(violationBuilder.addNode(anyString())).
                thenReturn(nodeBuilderDefinedContext);

        boolean isValid = validator.isValid(editedUserProfile, validatorContext);

        assertFalse(isValid, "New email is taken, so it must be invalid.");
    }
}
