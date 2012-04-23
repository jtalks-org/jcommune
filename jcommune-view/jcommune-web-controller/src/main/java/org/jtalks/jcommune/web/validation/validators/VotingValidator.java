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
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.web.util.PollUtil;
import org.jtalks.jcommune.web.validation.annotations.Voting;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Teterin
 *         Date: 14.04.12
 */


public class VotingValidator implements ConstraintValidator<Voting, Object> {
    private int minOptionsNumber;
    private int maxOptionsNumber;
    private String pollTitleName;
    private String pollTitleValue;
    private String pollOptionsName;
    private String pollOptionsValue;
    private String endingDateName;
    private String endingDateValue;
    private String message;

    private final String OPTIONS_NUMBER_MESSAGE = "{VotingOptionsNumber.message}";
    private final String FUTURE_DATE_MESSAGE = "{javax.validation.constraints.Future.message}";


    @Override
    public void initialize(Voting constraintAnnotation) {
        this.minOptionsNumber = constraintAnnotation.minOptionsNumber();
        this.maxOptionsNumber = constraintAnnotation.maxOptionsNumber();
        this.pollTitleName = constraintAnnotation.pollTitle();
        this.pollOptionsName = constraintAnnotation.pollOptions();
        this.endingDateName = constraintAnnotation.endingDate();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        getValidatedFields(value);

        return isVotingOptionsNumberValid(context)
                & isDateInStringFormatInFuture(context);
    }

    //TODO need to check implementation
    private boolean isVotingOptionsNumberValid(ConstraintValidatorContext context) {
        boolean result = false;
        List<PollOption> list;

        //TODO 16 Apr jk1 says: just count line breaks in string instead of all that code

        if (pollTitleValue == null) {
            result = true;
        } else {
            if (pollOptionsValue != null) {
                try {
                    list = PollUtil.parseOptions(pollOptionsValue);
                } catch (IOException e) {
                    list = new ArrayList<PollOption>(0);
                }

                if ((list.size() >= minOptionsNumber) || (list.size() <= maxOptionsNumber)) {
                    result = true;
                }
            }
        }

        if (!result) {
            constraintViolated(context, OPTIONS_NUMBER_MESSAGE, pollOptionsName);
        }

        return result;
    }

    private boolean isDateInStringFormatInFuture(ConstraintValidatorContext context) {
        boolean result;


        if (endingDateValue == null) {//null values are valid
            result = true;
        } else {
            DateTime date = PollUtil.parseDate(endingDateValue, Poll.DATE_FORMAT);
            result = date.isAfter(new DateTime());
        }

        if (!result) {
            constraintViolated(context, FUTURE_DATE_MESSAGE, endingDateName);
        }

        return result;
    }

    private void getValidatedFields(Object value) {
        try {
            pollTitleValue = BeanUtils.getProperty(value, pollTitleName);
            pollOptionsValue = BeanUtils.getProperty(value, pollOptionsName);
            endingDateValue = BeanUtils.getProperty(value, endingDateName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }


    private void constraintViolated(ConstraintValidatorContext context, String message, String fieldName) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addNode(fieldName)
                .addConstraintViolation();
    }
}
