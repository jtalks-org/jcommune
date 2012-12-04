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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.jtalks.jcommune.model.validation.annotations.PollItemNameLength;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PollItemNameLengthValidator implements ConstraintValidator<PollItemNameLength, String> {
    private int minLength;
    private int maxLenght;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PollItemNameLength constraintAnnotation) {
        this.minLength = constraintAnnotation.min();
        this.maxLenght = constraintAnnotation.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String pollItemName, ConstraintValidatorContext context) {
        Range range = new IntRange(minLength, maxLenght);
        int pollItemLength = pollItemName.length();
        return range.containsInteger(pollItemLength);
    }
}
