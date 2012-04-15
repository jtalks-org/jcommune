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


import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.web.util.PollUtil;
import org.jtalks.jcommune.web.validation.annotations.FutureDateInStringFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Alexandre Teterin
 */
public class FutureDateInStringFormatValidator implements ConstraintValidator<FutureDateInStringFormat, String> {


    @Override
    public void initialize(FutureDateInStringFormat constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result;


        if (value == null) {//null values are valid
            result = true;
        } else {
            DateTime date = PollUtil.parseDate(value, Poll.DATE_FORMAT);
            result = date.isAfter(new DateTime());
        }

        return result;
    }
}

