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
package org.jtalks.jcommune.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field to check it's value for existence in a database.
 * Specify HQL query in a parameter with one placeholder for the actual field value.
 * Validation succeds if result set returned is not empty.
 *
 * <p>For exmaple:
 *
 * <p><code>@Exists(hql = "from User user where user.email = ?", message = "{email.unknown}")
 * private String email<code/>
 *
 * <p>This snippet will pass the validation if and only if there is record with email value
 * equal to the one sprecified in varialbe.
 *
 * <p>Works only for sting variables as for now.
 *
 * @author Evgeniy Naumenko
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ExistenceValidator.class)
public @interface Exists {

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
     * Query to executed, must contain one placeholder for field value
     */
    String hql();
}
