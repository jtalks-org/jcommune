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


import org.jtalks.jcommune.web.validation.validators.ValidUserContactValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint for checking that user contact is valid (matches regular
 * expression for its type).
 * This constraint for use with JSR-303 validator.
 * <p/>
 * You must annotate your class with {@link ValidUserContact} annotation
 * You must fill in the parameters <code>field</code> and <code>storedTypeId</code>
 * field names to test. Fields must have getters. <code>field</code> fields
 * must be of type {@link java.lang.String}, <code>storedTypeId</code> must
 * be numeric
 * <p/>
 * Example:
 * Validate that <code>field1</code> is valid contact
 * {@code
 * &#064;ValidUserContact(field = "field1", fieldWithPattern = "typeId")
 * class Test {
 * private String field1;
 * private Integer typeId;
 * public String getField1() {
 * return field1;
 * }
 * public Integer getTypeId() {
 * return typeId;
 * }
 * }
 *
 * @author Vyacheslav Mishcheryakov
 * @see ValidUserContactValidator
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidUserContactValidator.class)
@Documented
public @interface ValidUserContact {

    /**
     * Message for display when validation fails.
     */
    String message() default "{org.jtalks.jcommune.web.validation.annotations.MatchesRegExp.message}";

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

    /**
     * Name of field to validate.
     */
    String field();

    /**
     * Path to property containing id of {@link org.jtalks.jcommune.model.entity.UserContactType} to get pattern
     * from it.
     */
    String storedTypeId();
}
