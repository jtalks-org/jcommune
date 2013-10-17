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

import org.jtalks.jcommune.web.validation.annotations.NullableNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Check that a string's trimmed length is not empty.
 *
 * @author Andrey Pogorelov
 */
public class NullableNotBlankValidator implements ConstraintValidator<NullableNotBlank, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(NullableNotBlank annotation) {
    }

    /**
     * Checks that the trimmed string is not empty.
     *
     * @param s                          The string to validate.
     * @param constraintValidatorContext context in which the constraint is evaluated.
     * @return Returns <code>true</code> if the string is <code>null</code> or the length of <code>s</code> between
     *         the specified
     *         <code>min</code> and <code>max</code> values (inclusive), <code>false</code> otherwise.
     */
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }

        return s.trim().length() > 0;
    }
}

