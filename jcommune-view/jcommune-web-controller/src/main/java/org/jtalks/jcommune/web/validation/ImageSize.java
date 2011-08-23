/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
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
 * Constraint for checking that image has allowable size.
 * This constraint for use with JSR-303 validator.
 * <p/>
 * You must annotate your field with {@link ImageSize} annotation
 * You must fill in the parameter <code>size</code>
 * field name to test.
 * Constraint can be used with any field types that have correct
 * </code>equals()</code> method.
 * <p/>
 * Example:
 * Validate that <code>size</code> are allowable.
 * {@code
 * class Test {
 * &#064;ImageSize(size=65)
 * private MultipartFile image;
 * public Test(MockMultipartFile image) {
 * this.image = image;
 * }
 * }
 * }
 *
 * @author Eugeny Batov
 * @see ImageSizeValidator
 */
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = ImageSizeValidator.class)
@Documented
public @interface ImageSize {
    /**
     * Message for display when validation fails.
     *
     * @return message when validation fails.
     */
    String message() default "{image.wrong.size}";

    /**
     * Groups element that specifies the processing groups with which the
     * constraint declaration is associated.
     *
     * @return array of groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload element that specifies the payload with which the the
     * constraint declaration is associated.
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Size of image for check dimension.
     *
     * @return size of image in kilobytes
     */
    int size();

}
