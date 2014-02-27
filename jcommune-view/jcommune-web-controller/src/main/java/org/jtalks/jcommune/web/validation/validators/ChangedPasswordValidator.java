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

import org.apache.commons.lang.ObjectUtils;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.EncryptionService;
import org.jtalks.jcommune.web.dto.UserSecurityDto;
import org.jtalks.jcommune.web.validation.annotations.ChangedPassword;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates if password set matches the current user's password.
 * This check is to be performed only if new password has been set.
 *
 * @author Evgeniy Naumenko
 */
public class ChangedPasswordValidator implements ConstraintValidator<ChangedPassword, UserSecurityDto> {

    private String message;

    private UserService userService;
    private EncryptionService encryptionService;

    /**
     * @param userService       to obtain current user logged in
     * @param encryptionService to encrypt passwords
     */
    @Autowired
    public ChangedPasswordValidator(UserService userService, EncryptionService encryptionService) {
        this.userService = userService;
        this.encryptionService = encryptionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ChangedPassword constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(UserSecurityDto dto, ConstraintValidatorContext context) {
        JCUser currentUser = userService.getCurrentUser();
        String editedUserName = getUsername(dto.getUserId());
        String currentUserName = currentUser.getUsername();
        boolean isWillBeChangedByOwner = ObjectUtils.equals(editedUserName, currentUserName);
        if (isWillBeChangedByOwner) {
            boolean result = dto.getNewUserPassword() == null;
            //we must compare the hashes, so we encrypt the entered value
            String enteredCurrentPassword = encryptionService.encryptPassword(dto.getCurrentUserPassword());
            result |= currentUser.getPassword().equals(enteredCurrentPassword);
            if (!result) {
                // add validation error to the field
                context.buildConstraintViolationWithTemplate(message)
                        .addNode("currentUserPassword")
                        .addConstraintViolation();
            }
            return result;
        }
        return true;
    }

    /**
     * Get username by user's id.
     *
     * @param userId user's id
     * @return an username
     */
    private String getUsername(long userId) {
        try {
            JCUser user = userService.get(userId);
            return user.getUsername();
        } catch (NotFoundException e) {
            return null;
        }
    }
}
