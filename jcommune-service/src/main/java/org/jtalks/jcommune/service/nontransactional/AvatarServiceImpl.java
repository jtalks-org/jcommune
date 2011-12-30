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
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.ImageUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

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
     * @throws ImageUploadException common avatar processing error
     */
    public String convertAvatarToBase64String(byte[] bytes) throws ImageUploadException {
        BufferedImage inputAvatar = imageUtils.convertByteArrayToImage(bytes);
        if (inputAvatar == null) {
            throw new ImageUploadException();
        }
        byte[] outputAvatar = imageUtils.preprocessImage(inputAvatar);
        return imageUtils.base64Coder(outputAvatar);
    }

    /**
     * Validate file format
     *
     * @param file for validation
     * @throws ImageFormatException invalid format avatar processing error
     */
    public void validateAvatarFormat(MultipartFile file) throws ImageFormatException {
        boolean isValid = false;

        String contentType = file.getContentType();
        for (ImageFormats format : ImageFormats.values()) {
            if (format.getContentType().equals(contentType)) {
                isValid = true;
            }
        }

        if (!isValid) {
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
        int BYTES_IN_KILOBYTE = 1024;
        boolean isValid = bytes.length / BYTES_IN_KILOBYTE < MAX_SIZE;
        if (!isValid) {
            throw new ImageSizeException();
        }
    }

}

/**
 * Stores all allowable formats and their content types.
 * PNG("image/png")
 *
 * @author Eugeny Batov
 */
enum ImageFormats {

    JPG("image/jpeg"), GIF("image/gif"), PNG("image/png");

    private String contentType;

    /**
     * Enum constructor.
     *
     * @param contentType - content type
     */
    ImageFormats(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return contentType - content type of chosen format
     */
    public String getContentType() {
        return contentType;
    }
}
