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
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.UserProfileDto;
import org.jtalks.jcommune.web.validation.annotations.ChangedEmail;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates if email change is valid. For detailed validation
 * description please refer to {@link ChangedEmail} documentation.
 *
 * @author Evgeniy Naumneko
 */
public class ChangedEmailValidator implements ConstraintValidator<ChangedEmail, UserProfileDto> {

    private UserService userService;
    private UserDao userDao;
    private String message;

    /**
     * @param userService to obtain current user logged in
     * @param userDao to check if desired email is already registered
     */
    @Autowired
    public ChangedEmailValidator(UserService userService, UserDao userDao) {
        this.userService = userService;
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ChangedEmail constraintAnnotation) {
        this.message = constraintAnnotation.message();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(UserProfileDto userProfile, ConstraintValidatorContext context) {
        String changedEmail = userProfile.getEmail();
        if (changedEmail == null) {
            // null emails checks are out of scope, pls use separate annotation for that
            return true;
        }
        String currentUserEmail = getEmailOfUser(userProfile.getUserId());
        if (ObjectUtils.equals(changedEmail, currentUserEmail)) {
            return true; // no changes in an email, that's ok
        } else {
            boolean userWithTheSameChangedEmailExists = userDao.getByEmail(changedEmail) != null;
            if(userWithTheSameChangedEmailExists){
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addNode("email")
                        .addConstraintViolation();
            }
            return !userWithTheSameChangedEmailExists;
        }
    }
    
    /**
     * Get email of user by user's id.
     * 
     * @param userId user's id
     * @return an email of user by user's id 
     */
    private String getEmailOfUser(long userId) {
        try {
            JCUser user = userService.get(userId);
            return user.getEmail();
        } catch (NotFoundException e) {
            return null;
        }  
    }
}
