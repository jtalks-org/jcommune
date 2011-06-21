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
    private Object fieldValue1;
    private Object fieldValue2;

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
     * @return {@code true} if validation successfull or false if fails
     * @throws IllegalStateException if property not found or doesnt have getter
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        getComparableFields(value);

        if (isNeitherSetted()) {
            return true;
        }

        boolean matches = isFieldsMatches();
        if (!matches) {
            constraintViolated(context);
        }
        return matches;
    }

    /**
     * Put constraint violation into context.
     *
     * @param context validator context
     */
    private void constraintViolated(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(failMessage)
                .addNode(secondPropertyName)
                .addConstraintViolation();
    }

    /**
     * @return {@code true} if field values are equals
     */
    private boolean isFieldsMatches() {
        return (fieldValue2 != null) && fieldValue2.equals(fieldValue1);
    }

    /**
     * @return {@code true} if neither fields are {@code null}
     */
    private boolean isNeitherSetted() {
        return (fieldValue2 == null) && (fieldValue1 == null);
    }

    /**
     * Retrieving comparable fields from object.
     * Throws {@code IllegalStateException} if field not found.
     *
     * @param value object from which we take values ​​of fields
     */
    private void getComparableFields(Object value) {
        try {
            fieldValue2 = BeanUtils.getProperty(value, firstPropertyName);
            fieldValue1 = BeanUtils.getProperty(value, secondPropertyName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}