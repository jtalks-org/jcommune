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
package org.jtalks.jcommune.model.validation.annotations;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.validation.validators.PollItemsSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * It's necessary to check the size of the list of poll items.
 * 
 * @author Anuar_Nurmakanov
 * @see Poll#MIN_ITEMS_NUMBER
 * @see Poll#MAX_ITEMS_NUMBER
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PollItemsSizeValidator.class)
public @interface PollItemsSize {
    /**
     * Resource bundle code for error message
     */
    String message() default "{poll.items.size}";

    /**
     * Groups settings for this validation constraint
     */
    Class<?>[] groups() default {};

    /**
     * Payload element that specifies the payload with which the the constraint
     * declaration is associated.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Min value for poll items number.
     */
    int min() default Poll.MIN_ITEMS_NUMBER;

    /**
     * Max value for poll items number.
     */
    int max() default Poll.MAX_ITEMS_NUMBER;
}