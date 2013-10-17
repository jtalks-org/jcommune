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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.tika.Tika;
import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Service class for uploaded image related operations
 *
 * @author Alexandre Teterin
 * @author Andrei Alikov
 */
public class ImageService {
    public static final String ICO_TYPE = "image/x-icon";
    private static final List<String> VALID_IMAGE_TYPES =
            Arrays.asList("image/jpeg", "image/png", "image/gif", ICO_TYPE);
    /**
     * user-friendly string with all valid image types
     */
    private static final String VALID_IMAGE_EXTENSIONS = "*.jpeg, *.jpg, *.gif, *.png, *.ico";

    private ImageConverter imageConverter;
    private Base64Wrapper base64Wrapper;
    private JCommuneProperty imageSizeProperty;
    private String defaultImagePath;
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    /**
     * Create ImageService instance
     *
     * @param imageConverter    object for image pre processing
     * @param base64Wrapper     to encode/decode image passed from the client side
     * @param defaultImagePath  class path to load default image
     * @param imageSizeProperty let us know the limitation of image max size
     */
    public ImageService(
            ImageConverter imageConverter,
            Base64Wrapper base64Wrapper,
            String defaultImagePath,
            JCommuneProperty imageSizeProperty) {
        this.imageConverter = imageConverter;
        this.base64Wrapper = base64Wrapper;
        this.imageSizeProperty = imageSizeProperty;
        this.defaultImagePath = defaultImagePath;
    }

    /**
     * Returns default image to be used when custom image is not set
     *
     * @return byte array-stored image
     */
    public byte[] getDefaultImage() {
        byte[] result;
        try {
            result = getFileBytes(defaultImagePath);
        } catch (IOException e) {
            result = new byte[0];
            LOGGER.error("Failed to load default image", e);
        }
        return result;
    }

    /**
     * Pre process image to fit maximum size and be in the target format and
     * convert the contents of the result image into String64 format
     *
     * @param bytes image for conversion
     * @return result string64 format
     * @throws org.jtalks.jcommune.service.exceptions.ImageProcessException
     *          common image processing error
     */
    public String preProcessAndEncodeInString64(byte[] bytes) throws ImageProcessException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        BufferedImage image = imageConverter.convertByteArrayToImage(bytes);
        if (image == null) { // something went wrong during conversion
            throw new ImageProcessException();
        }
        byte[] outputImage = imageConverter.preprocessImage(image);
        return base64Wrapper.encodeB64Bytes(outputImage);
    }

    /**
     * Validate file format
     *
     * @param file for validation, cannot be null
     * @throws org.jtalks.jcommune.service.exceptions.ImageFormatException
     *          invalid format image processing error
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
     * @throws org.jtalks.jcommune.service.exceptions.ImageSizeException
     *          invalid size image processing error
     */
    public void validateImageSize(byte[] bytes) throws ImageSizeException {
        Validate.notNull(bytes, "Incoming byte array cannot be null");
        int maxSize = imageSizeProperty.intValue();
        if (bytes.length > maxSize) {
            LOGGER.debug("File has too big size. Must be less than {} bytes", maxSize);
            throw new ImageSizeException(maxSize);
        }
    }

    /**
     * Gets content of the file by its classpath
     *
     * @param classPath classpath of the file to be loaded
     * @return content of the loaded file
     * @throws IOException
     */
    private byte[] getFileBytes(String classPath) throws IOException {
        byte[] result = new byte[0];
        ClassPathResource fileClassPathSource = new ClassPathResource(classPath);
        InputStream stream = null;
        try {
            stream = fileClassPathSource.getInputStream();
            result = new byte[stream.available()];
            Validate.isTrue(stream.read(result) > 0);
        } finally {
            IOUtils.closeQuietly(stream);
        }
        return result;
    }

    /**
     * Gets prefix for "src" attribute of the "img" tag representing the image format
     *
     * @return prefix for "src" attribute of the "img" tag representing the image format
     */
    public String getHtmlSrcImagePrefix() {
        return imageConverter.getHtmlSrcImagePrefix();
    }
}
