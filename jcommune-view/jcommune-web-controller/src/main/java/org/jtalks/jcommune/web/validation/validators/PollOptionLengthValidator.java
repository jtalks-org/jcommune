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


import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.validation.annotations.PollOptionLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * @author Alexandre Teterin
 */
public class PollOptionLengthValidator implements ConstraintValidator<PollOptionLength, String> {

    private int min;
    private int max;


    @Override
    public void initialize(PollOptionLength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //todo may be this should be changed to false
        boolean result = true;

        List<PollOption> list;

        if (value != null) {
            list = TopicDto.parseOptions(value);
            for (PollOption option : list) {
                if ((option.getName().length() < min) ||
                        (option.getName().length() > max)) {
                    result = false;
                    break;
                }

            }
        } else {
            result = false;
        }

        return result;
    }
}

