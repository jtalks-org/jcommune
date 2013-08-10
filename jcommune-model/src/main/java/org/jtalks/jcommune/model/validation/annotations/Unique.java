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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.validation.validators.UniqueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field to check it's value for non-existence in a database.
 * You should specify an Entity and a field to search for value in.
 *
 * <p>Works only for sting variables as for now.
 *
 * @author Evgeniy Naumenko
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueValidator.class)
public @interface Unique {

    /**
     * Resource bundle code for error message
     */
    String message();

    /**
     * Groups settings for this validation constraint
     */
    Class<?>[] groups() default {};

    /**
     * Payload, no used here
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Entity to be verified
     */
    Class<? extends Entity> entity();

    /**
     * Field to be checked
     */
    String field();
    
    /**
     * Ignore case or not when checking for uniqueness
     */
    boolean ignoreCase() default false;
}
