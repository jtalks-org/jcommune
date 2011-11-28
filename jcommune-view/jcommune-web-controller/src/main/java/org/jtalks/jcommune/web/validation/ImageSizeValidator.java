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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ImageSize}. Checks that image has allowable size.
 *
 * @author Eugeny Batov
 * @author Alexandre Teterin
 * @see ImageSize
 */
public class ImageSizeValidator implements ConstraintValidator<ImageSize, byte[]> {

    private int imageSize;
    private static final int BYTES_IN_KILOBYTE = 1024;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Initialize validator fields from annotation instance.
     *
     * @param constraintAnnotation {@link ImageSize} annotation from class
     * @see ImageSize
     */
    @Override
    public void initialize(ImageSize constraintAnnotation) {
        this.imageSize = constraintAnnotation.size();
    }

    /**
     * Check that file's size no more imageSize.
     *
     * @param context validation context
     * @return {@code true} if validation successfull or false if fails
     */
    @Override
    public boolean isValid(byte[] bytes, ConstraintValidatorContext context) {
        boolean result = false;

        if (bytes == null) {
            //assume that empty avatar is valid to avoid validation message when user doesn't load nothing
            return true;
        }

        return result = bytes.length / BYTES_IN_KILOBYTE < imageSize;
    }
}
