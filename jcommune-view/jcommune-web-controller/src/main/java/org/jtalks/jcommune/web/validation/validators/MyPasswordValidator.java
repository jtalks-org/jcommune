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

import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.validation.annotations.MyPassword;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates if password set matches the current user's password.
 *
 * @author Evgeniy Naumenko
 */
public class MyPasswordValidator implements ConstraintValidator<MyPassword, String> {


    private SecurityService service;

    /**
     * @param service to obtain current user logged in
     */
    @Autowired
    public MyPasswordValidator(SecurityService service) {
        this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(MyPassword constraintAnnotation) {
        //nothing here to init
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return service.getCurrentUser().getPassword().equals(value);
    }
}
