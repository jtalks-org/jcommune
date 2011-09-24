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

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link ImageFormat}. Checks that image has allowable format.
 *
 * @author Eugeny Batov
 * @see ImageFormat
 */
public class ImageFormatValidator implements ConstraintValidator<ImageFormat, MultipartFile> {
    private ImageFormats[] formats;

    @Override
    public void initialize(ImageFormat constraintAnnotation) {
        this.formats = constraintAnnotation.format();
    }

    /**
     * Validate object with {@link ImageFormat} annotation.
     * Check that file has extension jpg, png or gif
     *
     * @param multipartFile image that user want upload as avatar
     * @param context       validation context
     * @return {@code true} if validation successfull or false if fails
     */
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile.isEmpty()) {
            //assume that empty multipart file is valid to avoid validation message when user doesn't load nothing
            return true;
        }
        String contentType = multipartFile.getContentType();
        for (ImageFormats format : formats) {
            if (format.getContentType().equals(contentType)) {
                return true;
            }
        }
        return false;
    }
}
