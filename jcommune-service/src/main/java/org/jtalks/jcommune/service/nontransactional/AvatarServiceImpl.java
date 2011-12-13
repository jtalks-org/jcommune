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

import org.jtalks.jcommune.service.AvatarService;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Alexandre Teterin
 */
public class AvatarServiceImpl implements AvatarService {

    private final ImageUtils imageUtils;


    /**
     * Create AvatarServiceImpl instance
     *
     * @param imageUtils object for image processing
     */
    public AvatarServiceImpl(ImageUtils imageUtils) {
        this.imageUtils = imageUtils;
    }

    /**
     * Perform bytes data to string conversion
     *
     * @param bytes for conversion
     * @return result string
     * @throws IOException conversion problem
     */
    public String convertAvatarToBase64String(byte[] bytes) throws IOException {
        BufferedImage inputAvatar = imageUtils.convertByteArrayToImage(bytes);
        byte[] outputAvatar = imageUtils.preprocessImage(inputAvatar);
        return imageUtils.base64Coder(outputAvatar);
    }


    /**
     * Perform multipart file conversion to string
     *
     * @param file for conversion
     * @return result string
     */
    @Override
    public String convertAvatarToBase64String(MultipartFile file) {
        return null;
    }
}
