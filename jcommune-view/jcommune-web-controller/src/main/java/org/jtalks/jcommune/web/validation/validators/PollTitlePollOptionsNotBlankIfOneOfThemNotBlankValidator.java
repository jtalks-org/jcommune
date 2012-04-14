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


import org.apache.commons.beanutils.BeanUtils;
import org.jtalks.jcommune.web.validation.annotations.PollTitlePollOptionsNotBlankIfOneOfThemNotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Alexandre Teterin
 */
public class PollTitlePollOptionsNotBlankIfOneOfThemNotBlankValidator implements
        ConstraintValidator<PollTitlePollOptionsNotBlankIfOneOfThemNotBlank, Object> {
    private String pollTitleName;
    private String pollOptionsName;
    private String issue;
    private String message;
    private String pollTitleValue;
    private String pollOptionsValue;


    @Override
    public void initialize(PollTitlePollOptionsNotBlankIfOneOfThemNotBlank constraintAnnotation) {
        this.pollTitleName = constraintAnnotation.pollTitle();
        this.pollOptionsName = constraintAnnotation.pollOptions();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = false;

        getComparableFields(value);

        if ((pollTitleValue != null) && (pollOptionsValue == null)) {
            issue = pollOptionsName;
        }

        if ((pollTitleValue == null) && (pollOptionsValue != null)) {
            issue = pollTitleName;
        }

        if (issue == null) {
            result = true;
        }

        if (!result) {
            constraintViolated(context);
        }

        return result;
    }

    private void getComparableFields(Object value) {
        try {
            pollTitleValue = BeanUtils.getProperty(value, pollTitleName);
            pollOptionsValue = BeanUtils.getProperty(value, pollOptionsName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Put constraint violation into context.
     *
     * @param context validator context
     */
    private void constraintViolated(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addNode(issue)
                .addConstraintViolation();
    }
}

