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

import org.jtalks.jcommune.model.validation.validators.PollItemsNotEmptyIfTitleFilledValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * It's necessary to validate the poll to find case when poll items aren't filled,
 * but poll title is filled.
 * 
 * @author Anuar_Nurmakanov
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PollItemsNotEmptyIfTitleFilledValidator.class)
public @interface PollItemsNotEmptyIfTitleFilled {
   /**
    * Resource bundle code for error message
    */
   String message() default "{poll.items.not.blank}";

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
    * Get the name of validated poll items field. It's needed to construct constraint violation error (the annotation is
    * set for the class itself, but if problem appeared, we need to show error for particular field).
    */
   String pollItemsFieldName() default "pollItems";
}
