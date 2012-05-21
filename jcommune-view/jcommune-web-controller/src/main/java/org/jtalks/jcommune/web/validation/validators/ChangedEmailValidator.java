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

import org.jtalks.common.model.entity.User;
import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.dao.UserDao;
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
public class ChangedEmailValidator implements ConstraintValidator<ChangedEmail, String> {

    private SecurityService securityService;
    private UserDao userDao;

    /**
     * @param securityService to obtain current user logged in
     * @param userDao to check if desired email is already registered
     */
    @Autowired
    public ChangedEmailValidator(SecurityService securityService, UserDao userDao) {
        this.securityService = securityService;
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ChangedEmail constraintAnnotation) {
        //nothing to init here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            // null emails checks are out of scope, pls use separate annotation for that
            return true;
        }
        User user = securityService.getCurrentUser();
        if (user.getEmail().equals(value)) {
            return true; // no changes in an email, that's ok
        } else {
            User found = userDao.getByEmail(value);
            return found == null;
        }
    }
}
