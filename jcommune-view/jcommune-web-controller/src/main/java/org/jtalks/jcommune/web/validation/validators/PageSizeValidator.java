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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.ArrayUtils;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.web.validation.annotations.PageSize;

/**
 * Checks the the size of forum page. Size of forum page is
 * the count of rows that will be displayed to user.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class PageSizeValidator implements ConstraintValidator<PageSize, Integer> {
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(PageSize constraintAnnotation) {
        //we don't have any parameters
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value!= null && ArrayUtils.contains(JCUser.PAGE_SIZES_AVAILABLE, value);
    }
}
