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

import org.apache.commons.beanutils.PropertyUtils;
import org.jtalks.jcommune.model.validation.annotations.AtLeastOneFieldIsNotNull;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Validator for {@link AtLeastOneFieldIsNotNull}
 *
 * @author Dmitry S. Dolzhenko
 */
public class AtLeastOneFieldIsNotNullValidator
        implements ConstraintValidator<AtLeastOneFieldIsNotNull, Object> {

    private String[] fields;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(AtLeastOneFieldIsNotNull constraintAnnotation) {
        this.validateParameters(constraintAnnotation);
        this.fields = constraintAnnotation.fields();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object entity, ConstraintValidatorContext constraintValidatorContext) {
        try {
            for (String field : fields) {
                try {
                    if (PropertyUtils.getProperty(entity, field) != null) {
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    /*
                     * Method PropertyUtils.getProperty can get value of the field
                     * only if it has public accessor. Therefore, if the field doesn't have
                     * public accessor, we try to get its value manually.
                     */
                    Field fieldObject = entity.getClass().getDeclaredField(field);
                    fieldObject.setAccessible(true);
                    if (fieldObject.get(entity) != null) {
                        return true;
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            throw new ConstraintDeclarationException(e);
        }

        return false;
    }

    private void validateParameters(AtLeastOneFieldIsNotNull constraintAnnotation) {
        if (constraintAnnotation.fields().length == 0) {
            throw new IllegalArgumentException("The parameter \"fields\" must not be empty.");
        }
    }
}
