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

import org.jtalks.jcommune.web.validation.annotations.BbCodeAwareSize;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Extends default @Size annotation to ignore BB codes in string.
 * As for now, applicable to string values only.
 *
 * @author Evgeniy Naumenko
 */
public class BbCodeAwareSizeValidator implements ConstraintValidator<BbCodeAwareSize, String>, ApplicationContextAware {

    private int min;
    private int max;
    private ApplicationContext context;
    private BBCodeService bbCodeService;
    
    @Autowired
    public BbCodeAwareSizeValidator(BBCodeService bbCodeService) {
        this.bbCodeService = bbCodeService;
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
        if (value == null || value.length() > max) {
            return false;
        }
        int plainTextLength = removeBBCodes(value).trim().length();
        return (plainTextLength >= min);
    }

    /**
     * Removes all BB codes from the text given, simply cutting
     * out all [...]-style tags found
     *
     * @param source text to cleanup
     * @return plain text without BB tags
     */
    private String removeBBCodes(String source) {
        return getBBCodeService().stripBBCodes(source);
    }

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.context = ac;
    }
    
    private BBCodeService getBBCodeService() {
        if (bbCodeService == null) {
            bbCodeService = this.context.getBean(BBCodeService.class);
        }
        return bbCodeService;
    }
}
