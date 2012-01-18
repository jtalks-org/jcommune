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

import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Service for avatar related operations
 *
 * @author Alexandre Teterin
 */
public class AvatarService {


    private static final Set<String> VALID_IMAGE_TYPES = new HashSet<String>();
    /**
     * Max avatar size in bytes (to be moved in DB later)
     */
    public static final int MAX_SIZE = 4096 * 1024;

    private ImageUtils imageUtils;

    private String defaultAvatarPath;

    private static final Logger LOGGER = LoggerFactory.getLogger(AvatarService.class);

    /**
     * Create AvatarService instance
     *
     * @param imageUtils        object for image processing
     * @param defaultAvatarPath path to the default avatar image, to be replaced with image managed in Poulpe in future
     */
    public AvatarService(ImageUtils imageUtils, String defaultAvatarPath) {
        this.defaultAvatarPath = defaultAvatarPath;
        this.imageUtils = imageUtils;
        VALID_IMAGE_TYPES.add("image/jpeg");
        VALID_IMAGE_TYPES.add("image/png");
        VALID_IMAGE_TYPES.add("image/gif");
    }

    /**
     * Perform bytes data to string conversion
     *
     * @param bytes for conversion
     * @return result string
     * @throws ImageProcessException common avatar processing error
     */
    public String convertBytesToBase64String(byte[] bytes) throws ImageProcessException {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }

        BufferedImage image = imageUtils.convertByteArrayToImage(bytes);
        if (image == null) {
            throw new ImageProcessException();
        }

        byte[] outputAvatar = imageUtils.preprocessImage(image);
        return imageUtils.encodeB64(outputAvatar);
    }

    /**
     * Validate file format
     *
     * @param file for validation
     * @throws ImageFormatException invalid format avatar processing error
     */
    public void validateAvatarFormat(MultipartFile file) throws ImageFormatException {
        if (file == null) {
            throw new IllegalArgumentException();
        }

        if (!VALID_IMAGE_TYPES.contains(file.getContentType())) {
            throw new ImageFormatException();
        }
    }

    /**
     * Validate avatar size
     *
     * @param bytes array for validation
     * @throws ImageSizeException invalid size avatar processing error
     */
    public void validateAvatarSize(byte[] bytes) throws ImageSizeException {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }

        if (bytes.length > MAX_SIZE) {
            throw new ImageSizeException();
        }
    }

    public static Set<String> getValidImageTypes() {
        return VALID_IMAGE_TYPES;
    }

    /**
     * Returns default avatar to be used when custom user image is not set
     *
     * @return byte array-stored image
     */
    public byte[] getDefaultAvatar() {
        byte[] result = new byte[0];
        try {
            InputStream stream = AvatarService.class.getClassLoader().getResourceAsStream(defaultAvatarPath);
            result = new byte[stream.available()];
            stream.read(result);
        } catch (IOException e) {
            LOGGER.error("Failed to load default avatar", e);
        }
        return result;
    }
}
