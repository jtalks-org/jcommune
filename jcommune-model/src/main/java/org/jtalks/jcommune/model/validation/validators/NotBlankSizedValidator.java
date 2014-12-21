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
package org.jtalks.jcommune.model.validation.validators;

import org.jtalks.jcommune.model.validation.annotations.NotBlankSized;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link NotBlankSized} annotation
 *
 * @author Mikhail Stryzhonok
 */
public class NotBlankSizedValidator implements ConstraintValidator<NotBlankSized, String> {

    private int min;
    private int max;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(NotBlankSized constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    /**
     * Validates input string size
     *
     * @param value string with {@link NotBlankSized} annotation
     * @param context validation context
     *
     * @return true if string not null and has size between the specified boundaries (included).
     *         false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null
                && value.length() >= min
                && value.length() <= max;
    }
}
