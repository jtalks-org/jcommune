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
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Constraint for checking that avatar satisfies size, dimension and format constraints.
 * This constraint for use with JSR-303 validator.
 * <p/>
 * You must annotate your filed with {@link Avatar} annotation
 * Constraint can be used with any field types that have correct
 * </code>equals()</code> method.
 * <p/>
 * Example:
 * Validate that <code>field1</code> and <code>field2</code> are equals.
 * {@code
 * class Test {
 * &#064;Avatar
 * private MultipartFile image;
 * public Test(MockMultipartFile image) {
 * this.image = image;
 * }
 * }
 * }
 *
 * @author Eugeny Batov
 */
@ImageSize(size = Avatar.MAX_AVATAR_SIZE)
@ImageFormat(format = {ImageFormats.JPG, ImageFormats.PNG, ImageFormats.GIF}, message = "{avatar.wrong.format}")
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface Avatar {

    static final int MAX_AVATAR_SIZE = 4096;

    /**
     * Message for display when validation fails.
     */
    String message() default "{avatar.wrong}";

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
