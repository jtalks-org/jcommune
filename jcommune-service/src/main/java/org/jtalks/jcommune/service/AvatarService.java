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

package org.jtalks.jcommune.service;

import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.exceptions.ImageUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for avatar related operations
 *
 * @author Alexandre Teterin
 */
public interface AvatarService {

    int MAX_SIZE = 4096;


    /**
     * Perform bytes data to string conversion
     *
     * @param bytes for conversion
     * @return result string
     * @throws ImageUploadException common avatar processing error
     */
    String convertAvatarToBase64String(byte[] bytes) throws ImageUploadException;

    /**
     * Validate file format
     *
     * @param file for validation
     * @throws ImageFormatException invalid format avatar processing error
     */
    void validateAvatarFormat(MultipartFile file) throws ImageFormatException;

    /**
     * Validate avatar size
     *
     * @param bytes array for validation
     * @throws ImageSizeException invalid size avatar processing error
     */
    void validateAvatarSize(byte[] bytes) throws ImageSizeException;
}
