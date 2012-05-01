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
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.validation.annotations.ValidPoll;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexandre Teterin
 *         Date: 14.04.12
 */


public class PollValidator implements ConstraintValidator<ValidPoll, Object> {
    private int minItemsNumber;
    private int maxItemsNumber;
    private int minItemsLength;
    private int maxItemsLength;
    private String pollTitleName;
    private String pollTitleValue;
    private String pollItemsName;
    private String pollItemsValue;
    private String endingDateName;
    private String endingDateValue;
    private String message;
    private List<PollItem> items;

    private final String ITEMS_NUMBER_MESSAGE = "{VotingOptionsNumber.message}";
    private final String FUTURE_DATE_MESSAGE = "{javax.validation.constraints.Future.message}";
    private final String TITLE_NOT_BLANK_IF_ITEMS_NOT_BLANK_MESSAGE = "{PollTitleNotBlankIfPollItemsNotBlank.message}";
    private final String ITEMS_NOT_BLANK_IF_TITLE_NOT_BLANK_MESSAGE = "{PollItemsNotBlankIfPollTitleNotBlank.message}";
    public static final String DATE_NOT_BLANK_IF_TITLE_OR_ITEMS_NOT_BLANK_MESSAGE =
            "{DateNotBlankIfPollTitleOrItemsNotBlank.message}";
    private final String ITEM_LENGTH_MESSAGE = "{VotingItemLength.message}";


    @Override
    public void initialize(ValidPoll constraintAnnotation) {
        this.minItemsNumber = constraintAnnotation.minItemsNumber();
        this.maxItemsNumber = constraintAnnotation.maxItemsNumber();
        this.minItemsLength = constraintAnnotation.minItemsLength();
        this.maxItemsLength = constraintAnnotation.maxItemsLength();
        this.pollTitleName = constraintAnnotation.pollTitle();
        this.pollItemsName = constraintAnnotation.pollItems();
        this.endingDateName = constraintAnnotation.endingDate();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        getValidatedFields(value);

        return isVotingOptionsNumberValid(context)
                & isDateInStringFormatInFuture(context)
                & isTitleOrItemsOrDateNotBlankIfOneOfThemNotBlank(context)
                & isItemLengthValid(context);
    }

    private boolean isItemLengthValid(ConstraintValidatorContext context) {
        boolean result = true;

        //empty poll items field is valid
        if (!StringUtils.isNotBlank(pollItemsValue)) {
            return result;
        }

        for (PollItem item : items) {
            if ((item.getName().length() < minItemsLength) || (item.getName().length() > maxItemsLength)) {
                result = false;
                constraintViolated(context, ITEM_LENGTH_MESSAGE, pollItemsName);
            }
        }


        return result;
    }

    private boolean isTitleOrItemsOrDateNotBlankIfOneOfThemNotBlank(ConstraintValidatorContext context) {
        boolean result = true;
        //title is not blank & items are blank
        if (StringUtils.isNotBlank(pollTitleValue) && !StringUtils.isNotBlank(pollItemsValue)) {
            result = false;
            constraintViolated(context, ITEMS_NOT_BLANK_IF_TITLE_NOT_BLANK_MESSAGE,
                    pollItemsName);
        }

        //title is blank & items are not blank
        if (!StringUtils.isNotBlank(pollTitleValue) && StringUtils.isNotBlank(pollItemsValue)) {
            result = false;
            constraintViolated(context, TITLE_NOT_BLANK_IF_ITEMS_NOT_BLANK_MESSAGE,
                    pollTitleName);
        }

        //if title is not blank or items are not blank then date could not be blank
        if ((StringUtils.isNotBlank(pollTitleValue) || StringUtils.isNotBlank(pollItemsValue))
                && (!StringUtils.isNotBlank(endingDateValue))) {
            result = false;
            constraintViolated(context, DATE_NOT_BLANK_IF_TITLE_OR_ITEMS_NOT_BLANK_MESSAGE,
                    pollTitleName);
        }

        return result;

    }

    //TODO need to check implementation
    //Item should be more than one (should not be possible to create Poll with just one item,
    // error message appears on trying to save it)
    private boolean isVotingOptionsNumberValid(ConstraintValidatorContext context) {
        boolean result = false;

        if (!StringUtils.isNotBlank(pollTitleValue)) {
            //Poll title is empty so poll will not be created and it not need to check poll items number
            result = true;
        } else {
            if (StringUtils.isNotBlank(pollItemsValue)) {
                if ((items.size() >= minItemsNumber) || (items.size() <= maxItemsNumber)) {
                    result = true;
                }
            }
        }

        if (!result) {
            constraintViolated(context, ITEMS_NUMBER_MESSAGE, pollItemsName);
        }

        return result;
    }

    private boolean isDateInStringFormatInFuture(ConstraintValidatorContext context) {
        boolean result;


        if (endingDateValue == null) {//null values are valid
            result = true;
        } else {
            DateTime date = TopicDto.parseDate(endingDateValue, Poll.DATE_FORMAT);
            result = date.isAfter(new DateTime());
            System.out.println();
        }

        if (!result) {
            constraintViolated(context, FUTURE_DATE_MESSAGE, endingDateName);
        }

        return result;
    }

    private void getValidatedFields(Object value) {
        try {
            pollTitleValue = BeanUtils.getProperty(value, pollTitleName);
            pollItemsValue = BeanUtils.getProperty(value, pollItemsName);
            endingDateValue = BeanUtils.getProperty(value, endingDateName);
            if (StringUtils.isNotBlank(pollItemsValue)) {
                items = TopicDto.parseItems(pollItemsValue);
            } else {
                items = new ArrayList<PollItem>(0);
            }
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
