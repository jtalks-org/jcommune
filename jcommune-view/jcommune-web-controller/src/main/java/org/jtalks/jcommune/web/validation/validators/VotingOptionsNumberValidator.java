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
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.web.util.PollUtil;
import org.jtalks.jcommune.web.validation.annotations.VotingOptionsNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Teterin
 *         Date: 14.04.12
 */


public class VotingOptionsNumberValidator implements ConstraintValidator<VotingOptionsNumber, Object> {
    private int min;
    private int max;
    private String pollTitleName;
    private String pollTitleValue;
    private String pollOptionsName;
    private String pollOptionsValue;

    private String message;

    @Override
    public void initialize(VotingOptionsNumber constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
        this.pollTitleName = constraintAnnotation.pollTitle();
        this.pollOptionsName = constraintAnnotation.pollOptions();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        boolean result = false;
        List<PollOption> list;

        getValidatedFields(value);

        if (pollTitleValue == null) {
            result = true;
        } else {
            if (pollOptionsValue != null) {
                try {
                    list = PollUtil.parseOptions(pollOptionsValue);
                } catch (IOException e) {
                    list = new ArrayList<PollOption>(0);
                }

                if ((list.size() >= min) || (list.size() <= max)) {
                    result = true;
                }
            }
        }

        if (!result) {
            constraintViolated(context);
        }

        return result;
    }

    private void getValidatedFields(Object value) {
        try {
            pollTitleValue = BeanUtils.getProperty(value, pollTitleName);
            pollOptionsValue = BeanUtils.getProperty(value, pollOptionsName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }


    private void constraintViolated(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addNode(pollOptionsName)
                .addConstraintViolation();
    }
}
