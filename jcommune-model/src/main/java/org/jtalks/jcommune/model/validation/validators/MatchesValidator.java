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


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.jtalks.jcommune.model.validation.annotations.Matches;

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
    private String fieldValue1;
    private String fieldValue2;

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
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        getComparableFields(value);

        boolean matches = StringUtils.equals(fieldValue1, fieldValue2);
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