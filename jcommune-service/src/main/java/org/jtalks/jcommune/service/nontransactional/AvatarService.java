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
package org.jtalks.jcommune.service.nontransactional;

import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Service for avatar related operations
 *
 * @author Alexandre Teterin
 */
public class AvatarService extends BaseImageService {

    private String defaultAvatarPath;
    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    /**
     * Create AvatarService instance
     *
     * @param imageUtils        object for image processing
     * @param base64Wrapper     to encode/decode avatar passed from the client side
     * @param defaultAvatarPath path to the default avatar image, to be replaced with image managed in Poulpe in future
     * @param avatarSizeProperty let us know the limitation of avatar max size
     */
    public AvatarService(
            ImageUtils imageUtils,
            Base64Wrapper base64Wrapper,
            String defaultAvatarPath,
            JCommuneProperty avatarSizeProperty) {
        super(imageUtils, base64Wrapper, avatarSizeProperty);
        this.defaultAvatarPath = defaultAvatarPath;
    }



    /**
     * Returns default avatar to be used when custom user image is not set
     *
     * @return byte array-stored image
     */
    public byte[] getDefaultAvatar() {
        byte[] result = new byte[0];
        InputStream stream = AvatarService.class.getClassLoader().getResourceAsStream(defaultAvatarPath);
        try {
            result = new byte[stream.available()];
            Validate.isTrue(stream.read(result) > 0);
        } catch (IOException e) {
            LOGGER.error("Failed to load default avatar", e);
        }
        return result;
    }

}
