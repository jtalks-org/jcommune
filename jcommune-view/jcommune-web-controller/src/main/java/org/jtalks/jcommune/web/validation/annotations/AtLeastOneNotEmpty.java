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
package org.jtalks.jcommune.web.validation.annotations;

import org.jtalks.jcommune.web.validation.validators.AtLeastOneNotEmptyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that at lest of specified string fields in class is not empty
 *
 * @author Mikhail Stryzhonok
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNotEmptyValidator.class)
@Documented
public @interface AtLeastOneNotEmpty {

    /**
     * Array with names of fields to check
     */
    String[] fieldNames();

    /**
     * Message for display when validation fails.
     */
    String message() default "";

    /**
     * Groups element that specifies the processing groups with which the
     * constraint declaration is associated.
     */
    Class<?>[] groups() default {};

    /**
     * Payload element that specifies the payload with which the the
     * constraint declaration is associated.
     */
    Class<? extends Payload>[] payload() default {};
}
