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
import org.jtalks.jcommune.model.validation.validators.TopicDraftNumberOfPollItemsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Constraint for checking number of poll items in draft topic.
 *
 * @author Dmitry S. Dolzhenko
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TopicDraftNumberOfPollItemsValidator.class)
public @interface TopicDraftNumberOfPollItems {
    /**
     * Resource bundle code for error message
     */
    String message() default "{poll.items.size}";

    /**
     * Groups settings for this validation constraint
     */
    Class<?>[] groups() default {};

    /**
     * Payload element that specifies the payload with which the the
     * constraint declaration is associated.
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
