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
import org.jtalks.jcommune.web.validation.annotations.NotMe;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates field with a username set if it matches the current user
 * logged in. The main intention here is to disallow for user to set
 * it's own name as a field value.
 */
public class NotMeValidator implements ConstraintValidator<NotMe, String> {

    private UserService service;

    /**
     * @param userService  to get the current user
     */
    @Autowired
    public NotMeValidator(UserService userService) {
        this.service = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(NotMe constraintAnnotation) {
        //no configuration is required
    }

    /**
     * @param value filed value to be validated
     * @param context validator context, not used here
     * @return true if user specified is not the current one logged in
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        JCUser user = service.getCurrentUser();
        return user.isAnonymous() || !user.getUsername().equals(value);
    }
}
