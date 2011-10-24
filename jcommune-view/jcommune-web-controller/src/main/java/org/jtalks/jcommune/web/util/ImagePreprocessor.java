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
package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.service.exceptions.InvalidImageException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Class for preparing image to save.
 *
 * @author Eugeny Batov
 */
@Component
public class ImagePreprocessor {

    /**
     * Prepares image to save- converts multipart file to image, resizes image to required dimension, converts image to
     * byte array.
     *
     * @param multipartFile input multipart file
     * @param  maxWidth max width of modified image
     * @param  maxHeight max height of modified image
     * @return prepared image byte array
     * @throws java.io.IOException - throws if an I/O error occurs
     * @throws InvalidImageException - throws if image is invalid or image's format is not allowable
     */
    public byte[] preprocessImage(MultipartFile multipartFile, int maxWidth, int maxHeight) throws IOException,
            InvalidImageException {
        if (multipartFile.isEmpty()) {
            //assume that empty multipart file is valid to avoid validation message when user doesn't load nothing
            return null;
        }
        Image image = ImageUtil.convertMultipartFileToImage(multipartFile);
        if (image == null) {
            throw new InvalidImageException("Unable to convert multipart data to image");
        }
        image = ImageUtil.resizeImage((BufferedImage) image, ImageUtil.IMAGE_JPEG, maxWidth, maxHeight);
        return ImageUtil.convertImageToByteArray(image);
    }
}
