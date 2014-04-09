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

import org.jtalks.jcommune.model.validation.annotations.SizeAndNotBlank;

/**
 * Check that a string's trimmed length is not empty
 * and row size is between the max and min
 *
 * @author Alexandra Khekhneva
 */
public class SizeAndNotBlankValidator implements ConstraintValidator<SizeAndNotBlank, String> {

     private int min;
     private int max;


     /**
      * {@inheritDoc}
      */
     @Override
     public void initialize(SizeAndNotBlank annotation) {
         this.min = annotation.min();
         this.max = annotation.max();
    }

     /**
      * {@inheritDoc}
      */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if ( s == null ) {
            return true;
        }
        if  ((s.trim().length() >= min) && (s.trim().length() <= max)) {
            return true;
        }
        return false;
    }
}
