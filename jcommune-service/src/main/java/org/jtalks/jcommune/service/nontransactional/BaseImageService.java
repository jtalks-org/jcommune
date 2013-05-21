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
import org.apache.tika.Tika;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Service class for image related operations
 */
public class BaseImageService {
    private static final List<String> VALID_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    /** user-friendly string with all valid image types */
    private static final String VALID_IMAGE_EXTENSIONS = "*.jpeg, *.jpg, *.gif, *.png";

    private ImageUtils imageUtils;
    private Base64Wrapper base64Wrapper;
    private JCommuneProperty imageSizeProperty;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseImageService.class);

    /**
     * Create BaseImageService instance
     *
     * @param imageUtils        object for image processing
     * @param base64Wrapper     to encode/decode image passed from the client side
     * @param imageSizeProperty let us know the limitation of image max size
     */
    public BaseImageService(
            ImageUtils imageUtils,
            Base64Wrapper base64Wrapper,
            JCommuneProperty imageSizeProperty) {
        this.imageUtils = imageUtils;
        this.base64Wrapper = base64Wrapper;
        this.imageSizeProperty = imageSizeProperty;
    }

    /**
     * Perform bytes data to string conversion
     *  (todo: wtf? it does tons of things, correct method name and description)
     * @param bytes for conversion
     * @return result string
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException common image processing error
     */
    public String convertBytesToBase64String(byte[] bytes) throws ImageProcessException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        BufferedImage image = imageUtils.convertByteArrayToImage(bytes);
        if (image == null) { // something went wrong during conversion
            throw new ImageProcessException();
        }
        byte[] outputImage = imageUtils.preprocessImage(image);
        return base64Wrapper.encodeB64Bytes(outputImage);
    }

    /**
     * Validate file format
     *
     * @param file for validation, cannot be null
     * @throws org.jtalks.jcommune.service.exceptions.ImageFormatException invalid format image processing error
     */
    public void validateImageFormat(MultipartFile file) throws ImageFormatException {
        Validate.notNull(file, "file argument array cannot be null");
        if (!VALID_IMAGE_TYPES.contains(file.getContentType())) {
            LOGGER.debug("Wrong file extension. May be only {}", VALID_IMAGE_EXTENSIONS);
            throw new ImageFormatException(VALID_IMAGE_EXTENSIONS);
        }
    }

    /**
     * Image byte array data format
     *
     * @param bytes for validation
     * @throws ImageFormatException invalid format image processing error
     */
    public void validateImageFormat(byte[] bytes) throws ImageFormatException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        Tika tika = new Tika();
        InputStream input = new ByteArrayInputStream(bytes);
        try {
            String type = tika.detect(input);
            if (!VALID_IMAGE_TYPES.contains(type)) {
                LOGGER.debug("Wrong file extension. May be only {}", VALID_IMAGE_EXTENSIONS);
                throw new ImageFormatException(VALID_IMAGE_EXTENSIONS);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to handle image ByteArrayInputStream", e);
        }
    }

    /**
     * Validate image size
     *
     * @param bytes array for validation
     * @throws org.jtalks.jcommune.service.exceptions.ImageSizeException invalid size image processing error
     */
    public void validateImageSize(byte[] bytes) throws ImageSizeException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        int maxSize = imageSizeProperty.intValue();
        if (bytes.length > maxSize) {
            LOGGER.debug("File has too big size. Must be less than {} bytes", maxSize);
            throw new ImageSizeException(maxSize);
        }
    }
}
