/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.validation;


import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Matches}. Checks equality of two properties.
 *
 * @author Kirill Afonin
 * @see Matches
 */
public class MatchesValidator implements ConstraintValidator<Matches, Object> {

    private String firstPropertyName;
    private String secondPropertyName;
    private String failMessage;

    /**
     * Initialize validator fields from annotation instance.
     *
     * @param constraintAnnotation {@link Matches} annotation from class
     * @see Matches
     */
    @Override
    public void initialize(Matches constraintAnnotation) {
        this.firstPropertyName = constraintAnnotation.field();
        this.secondPropertyName = constraintAnnotation.verifyField();
        this.failMessage = constraintAnnotation.message();
    }

    /**
     * Validate object with {@link Matches} annotation.
     *
     * @param value   object with {@link Matches} annotation
     * @param context validation context
     * @return {@code true}if validation successfull or false if fails
     * @throws IllegalStateException if property not found or doesnt have getter
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object fieldObj;
        try {
            fieldObj = BeanUtils.getProperty(value, firstPropertyName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        Object verifyFieldObj;
        try {
            verifyFieldObj = BeanUtils.getProperty(value, secondPropertyName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        boolean neitherSet = (fieldObj == null) && (verifyFieldObj == null);

        if (neitherSet) {
            return true;
        }

        boolean matches = (fieldObj != null) && fieldObj.equals(verifyFieldObj);

        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(failMessage)
                    .addNode(secondPropertyName)
                    .addConstraintViolation();
        }

        return matches;
    }
}