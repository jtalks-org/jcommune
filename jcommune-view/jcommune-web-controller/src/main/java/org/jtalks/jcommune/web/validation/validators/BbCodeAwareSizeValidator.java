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

import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Extends default @Size annotation to ignore BB codes in string.
 * As for now, applicable to string values only.
 *
 * @author Evgeniy Naumenko
 */
public class BbCodeAwareSizeValidator implements ConstraintValidator<BbCodeAwareSize, String> {

    private BBCodeService service;

    private int min;
    private int max;

    /**
     * @param service service to remove BB-codes out of a string
     */
    @Autowired
    public BbCodeAwareSizeValidator(BBCodeService service) {
        this.service = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(BbCodeAwareSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        int plainTextLength = service.removeBBCodes(value).trim().length();
        return (plainTextLength >= min) && (plainTextLength <= max);
    }
}
