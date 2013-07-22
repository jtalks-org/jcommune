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

import org.jtalks.jcommune.web.validation.validators.ChangedEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates if email change is valid. Applicable for String fields only.
 * Checks the following conditions:
 * <p> - If user is logged in to perform the operation
 * <p> - If email is equal to the email already set for this user. That
 * means no change and is perfectly valid
 * <p> - New email is set and no other user with this new email exists
 * in a database, so we can safely assign a current user desired email
 *
 * @author Evgeniy Naumenko
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ChangedEmailValidator.class)
public @interface ChangedEmail {
    /**
     * Resource bundle code for error message
     */
    String message() default "{user.email.already_exists}";

    /**
     * Groups settings for this validation constraint
     */
    Class<?>[] groups() default {};

    /**
     * Payload, not used here
     */
    Class<? extends Payload>[] payload() default {};
}
