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

import org.jtalks.jcommune.model.validation.validators.MatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint for checking that two properties are equal.
 * This constraint for use with JSR-303 validator.
 * <p/>
 * You must annotate your class with {@link Matches} annotation
 * You must fill in the parameters <code>field</code> and <code>verifyField</code>
 * field names to test for equality. Fields must have getters.
 * Constraint can be used with any field types that have correct
 * </code>equals()</code> method.
 * <p/>
 * Example:
 * Validate that <code>field1</code> and <code>field2</code> are equals.
 * {@code
 * &#064;Matches(field = "field1", verifyField = "field2")
 * class Test {
 * private String field1;
 * private String field2;
 * public String getField1() {
 * return field1;
 * }
 * public String getField2() {
 * return field2;
 * }
 * }
 * }
 *
 * @author Kirill Afonin
 * @see MatchesValidator
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MatchesValidator.class)
@Documented
public @interface Matches {
    /**
     * Message for display when validation fails.
     */
    String message() default "{org.jtalks.jcommune.model.validation.annotations.Matches.message}";

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
     * First property name for check equality.
     */
    String field();

    /**
     * Second property name for check equality.
     */
    String verifyField();
}