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
import org.jtalks.jcommune.web.util.PollUtil;
import org.jtalks.jcommune.web.validation.annotations.DateInStringFormat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Alexandre Teterin
 */
public class DateInStringFormatValidator implements ConstraintValidator<DateInStringFormat, String> {

    private String format;


    @Override
    public void initialize(DateInStringFormat constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean result = false;

        DateTime date = PollUtil.parseDate(value, format);

        if (!new DateTime(0).equals(date)) {
            result = true;
        }

        return result;
    }
}

