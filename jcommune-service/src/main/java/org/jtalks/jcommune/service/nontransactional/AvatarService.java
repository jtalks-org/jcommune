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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Service for avatar related operations
 *
 * @author Alexandre Teterin
 */
public class AvatarService {

    private static final List<String> VALID_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif");
    /** user-friendly string with all valid image types */
    private static final String VALID_IMAGE_EXTENSIONS = "*.jpeg, *.jpg, *.gif, *.png";
    public static final String HTTP_HEADER_DATETIME_PATTERN = "E, dd MMM yyyy HH:mm:ss z";
    
    private ImageUtils imageUtils;
    private Base64Wrapper base64Wrapper;
    private String defaultAvatarPath;
    private JCommuneProperty avatarSizeProperty;

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
        this.defaultAvatarPath = defaultAvatarPath;
        this.imageUtils = imageUtils;
        this.base64Wrapper = base64Wrapper;
        this.avatarSizeProperty = avatarSizeProperty;
    }

    /**
     * Perform bytes data to string conversion
     *  (todo: wtf? it does tons of things, correct method name and description)
     * @param bytes for conversion
     * @return result string
     * @throws ImageProcessException common avatar processing error
     */
    public String convertBytesToBase64String(byte[] bytes) throws ImageProcessException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        BufferedImage image = imageUtils.convertByteArrayToImage(bytes);
        if (image == null) { // something went wrong during conversion
            throw new ImageProcessException();
        }
        byte[] outputAvatar = imageUtils.preprocessImage(image);
        return base64Wrapper.encodeB64Bytes(outputAvatar);
    }

    /**
     * Validate file format
     *
     * @param file for validation, cannot be null
     * @throws ImageFormatException invalid format avatar processing error
     */
    public void validateAvatarFormat(MultipartFile file) throws ImageFormatException {
        Validate.notNull(file, "file argument array cannot be null");
        if (!VALID_IMAGE_TYPES.contains(file.getContentType())) {
            throw new ImageFormatException(VALID_IMAGE_EXTENSIONS);
        }
    }

    /**
     * Validate byte array data format
     *
     * @param bytes for validation
     * @throws ImageFormatException invalid format avatar processing error
     */
    public void validateAvatarFormat(byte[] bytes) throws ImageFormatException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        Tika tika = new Tika();
        InputStream input = new ByteArrayInputStream(bytes);
        try {
            String type = tika.detect(input);
            if (!VALID_IMAGE_TYPES.contains(type)) {
                throw new ImageFormatException(VALID_IMAGE_EXTENSIONS);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to handle avatar ByteArrayInputStream", e);
        }
    }

    /**
     * Validate avatar size
     *
     * @param bytes array for validation
     * @throws ImageSizeException invalid size avatar processing error
     */
    public void validateAvatarSize(byte[] bytes) throws ImageSizeException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        int maxSize = avatarSizeProperty.intValue();
        if (bytes.length > maxSize) {
            throw new ImageSizeException(maxSize);
        }
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
    
    /**
     * Check 'If-Modified-Since' header in the request and converts it to 
     * {@link java.util.Date} representation
     * @param ifModifiedSinceHeader - value of 'If-Modified-Since' header in 
     *      string form
     * @return If-Modified-Since header or Jan 1, 1970 if it is not set or
     *      can't be parsed
     */
    public Date getIfModifiedSineDate(String ifModifiedSinceHeader) {
        Date ifModifiedSinceDate = new Date(0);
        if (ifModifiedSinceHeader != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(
                        HTTP_HEADER_DATETIME_PATTERN,
                        Locale.US); 
                ifModifiedSinceDate = dateFormat.parse(ifModifiedSinceHeader);
            } catch (ParseException e) {
                // in case date is wrong or not specified date will be Jan 1, 1970.
            }
        }
        
        return ifModifiedSinceDate;
    }
}
