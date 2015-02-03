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

import net.sf.ehcache.hibernate.management.impl.BeanUtils;
import org.jtalks.jcommune.web.validation.annotations.AtLeastOneNotEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Mikhail Stryzhonok
 */
public class AtLeastOneNotEmptyValidator implements ConstraintValidator<AtLeastOneNotEmpty, Object> {

    private String[] fieldNames;

    @Override
    public void initialize(AtLeastOneNotEmpty constraintAnnotation) {
        fieldNames = constraintAnnotation.fieldNames();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String name : fieldNames) {
            Object property = BeanUtils.getBeanProperty(value, name);
            if (property instanceof String && ((String) property).trim().length() > 0) {
                return true;
            }
        }
        return false;
    }
}
