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

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Simple implementation of {@link org.springframework.validation.Validator} interface.
 * Should be used in tests with Spring MockMVC framework
 *
 * @author Mikhail Stryzhonok
 */
public class ValidatorStub implements Validator {

    private String[] errorFields;

    public ValidatorStub(String ... errorFields) {
        this.errorFields = errorFields;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errorFields != null) {
            for (String field : errorFields) {
                errors.rejectValue(field, "Test Error");
            }
        }
    }
}
