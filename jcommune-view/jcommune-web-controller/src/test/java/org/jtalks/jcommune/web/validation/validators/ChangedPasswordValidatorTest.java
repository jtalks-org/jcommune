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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.web.dto.UserSecurityDto;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderDefinedContext;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class ChangedPasswordValidatorTest {
    private static final String PROFILE_OWNER_NAME = "owner";

	@Mock
	private UserService userService;
	@Mock
	private EncryptionService encryptionService;
	@Mock
	private ConstraintValidatorContext validatorContext;
	@Mock
	private ConstraintViolationBuilder violationBuilder;
	@Mock 
	private NodeBuilderDefinedContext nodeBuilderDefinedContext;
	private ChangedPasswordValidator validator;
	private String userCurrentPassword = "password";
	private String userNewPassword = "new_password";
    private UserSecurityDto userSecurityDto;

	@BeforeMethod
	public void init() throws NotFoundException {
		initMocks(this);
        userSecurityDto = new UserSecurityDto();
		when(userService.getCurrentUser()).thenReturn(
                new JCUser(PROFILE_OWNER_NAME, "email", userCurrentPassword));
        userSecurityDto.setUserId(1L);
		validator = new ChangedPasswordValidator(userService, encryptionService);

        userSecurityDto.setCurrentUserPassword(userCurrentPassword);
        userSecurityDto.setNewUserPassword(userNewPassword);
        when(userService.get(1L)).thenReturn(new JCUser(PROFILE_OWNER_NAME, "email", userCurrentPassword));
	}
	
	@Test
    public void editedByModeratorShouldNotCheckCurrentPassword() throws NotFoundException {
        userSecurityDto.setUserId(2L);

        when(userService.get(2L)).thenReturn(new JCUser("moderator", "email", userCurrentPassword));

        boolean isValid = validator.isValid(userSecurityDto, validatorContext);
        
        assertTrue(isValid, "If moderator edits user's profile, we mustn't check current password.");
    }

    @Test
    public void editedNotExistingUserShouldNotCheckPassword() throws NotFoundException {
        when(userService.get(1L)).thenThrow(new NotFoundException());

        boolean isValid = validator.isValid(userSecurityDto, validatorContext);

        assertTrue(isValid, "If someone try to edit not existing user's profile, we shouldn't check current password.");
    }

	@Test
	public void editedByOwnerWithNewPasswordAsNullShouldBeValid() {
        userSecurityDto.setNewUserPassword(null);
		
		boolean isValid = validator.isValid(userSecurityDto, validatorContext);
		
		assertTrue(isValid, "The null password is not valid.");
	}
	
	@Test
	public void editedByOwnerWithCorrectCurrentPasswordShouldBeValid() {
	    String currentUserPassword = userSecurityDto.getCurrentUserPassword();
	    when(encryptionService.encryptPassword(currentUserPassword)).
	        thenReturn(currentUserPassword);

	    boolean isValid = validator.isValid(userSecurityDto, validatorContext);
		
	    assertTrue(isValid, "The old password is correct, but the check fails.");
	}
	
	@Test
	public void editedByOwnerWithIncorrectCurrentPasswordShouldNotBeValid() {
	    String incorrectCurrentPassword = "other_password";
        userSecurityDto.setCurrentUserPassword(incorrectCurrentPassword);
		when(encryptionService.encryptPassword(incorrectCurrentPassword)).
            thenReturn(incorrectCurrentPassword);
		when(validatorContext.buildConstraintViolationWithTemplate(null)).
				thenReturn(violationBuilder);
		when(violationBuilder.addNode(anyString())).
				thenReturn(nodeBuilderDefinedContext);
		boolean isValid = validator.isValid(userSecurityDto, validatorContext);
		
		assertFalse(isValid, "The old password isn't correct, but the check passed.");
		verify(validatorContext).buildConstraintViolationWithTemplate(null);
	}
}
