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

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.awt.*;
import java.io.IOException;

/**
 * @author Eugeny Batov
 */
public class AvatarSizeValidator implements ConstraintValidator<AvatarSize, MultipartFile> {

    private static final int AVATAR_WIDTH = 100;
    private static final int AVATAR_HEIGHT = 100;

    @Override
    public void initialize(AvatarSize avatarSize) {
        //nothing to do
    }

    /**
     * @param multipartFile
     * @param context validation context
     * @return {@code true} if validation successfull or false if fails
     */
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile.getOriginalFilename().equals("")) {
            return true;
        }
        Image avatar = null;
        try {
            avatar = ImageIO.read(multipartFile.getInputStream());
        } catch (IOException e) {
            return false;
        }
        return (avatar == null) ? false : avatar.getHeight(null) == AVATAR_WIDTH && avatar.getHeight(null) == AVATAR_HEIGHT;
    }
}
