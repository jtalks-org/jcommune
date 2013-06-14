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

package org.jtalks.jcommune.web.controller;


import org.apache.commons.lang.time.DateFormatUtils;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.BaseImageService;
import org.jtalks.jcommune.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseReason;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Locale;

public class ImageUploadController {

    private MessageSource messageSource;

    static final String WRONG_FORMAT_RESOURCE_MESSAGE = "image.wrong.format";
    static final String WRONG_SIZE_RESOURCE_MESSAGE = "image.wrong.size";
    static final String COMMON_ERROR_RESOURCE_MESSAGE = "avatar.500.common.error";

    protected static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";

    public ImageUploadController(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Handles an exception that is thrown when the image has incorrect size.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageSizeException.class)
    @ResponseBody
    public FailJsonResponse handleImageSizeException(ImageSizeException e, Locale locale) {
        Object[] parameters = new Object[]{e.getMaxSize()};
        String errorMessage = messageSource.getMessage(WRONG_SIZE_RESOURCE_MESSAGE, parameters, locale);
        return new FailJsonResponse(JsonResponseReason.VALIDATION, errorMessage);
    }

    /**
     * Handles an exception that is thrown when the image has incorrect format.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageFormatException.class)
    @ResponseBody
    public FailJsonResponse handleImageFormatException(ImageFormatException e, Locale locale) {
        Object[] validImageTypes = new Object[]{e.getValidImageTypes()};
        String errorMessage = messageSource.getMessage(WRONG_FORMAT_RESOURCE_MESSAGE, validImageTypes, locale);
        return new FailJsonResponse(JsonResponseReason.VALIDATION, errorMessage);
    }

    /**
     * Handles common exception that can occur when loading an image.
     *
     * @param e      exception
     * @param locale locale, it's needed for error message localization
     * @return DTO, that contains information about error, it will be converted to JSON
     */
    @ExceptionHandler(value = ImageProcessException.class)
    @ResponseBody
    public FailJsonResponse handleImageProcessException(ImageProcessException e, Locale locale) {
        String errorMessage = messageSource.getMessage(COMMON_ERROR_RESOURCE_MESSAGE, null, locale);
        return new FailJsonResponse(JsonResponseReason.INTERNAL_SERVER_ERROR, errorMessage);
    }

    /**
     * Gets service for resolving messages
     * @return service for resolving messages
     */
    public MessageSource getMessageSource() {
        return messageSource;
    }

    /**
     * Sets up avatar cache related headers.
     * @param response - HTTP response object where set headers
     * @param avatarLastModificationTime - last modification time of avatar
     */
    protected void setupAvatarHeaders(HttpServletResponse response,
                                    Date avatarLastModificationTime) {
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "public");
        response.addHeader("Cache-Control", "must-revalidate");
        response.addHeader("Cache-Control","max-age=0");
        String formattedDateExpires = DateFormatUtils.format(
                new Date(System.currentTimeMillis()),
                BaseImageService.HTTP_HEADER_DATETIME_PATTERN, Locale.US);
        response.setHeader("Expires", formattedDateExpires);

        String formattedDateLastModified = DateFormatUtils.format(
                avatarLastModificationTime,
                BaseImageService.HTTP_HEADER_DATETIME_PATTERN, Locale.US);
        response.setHeader("Last-Modified", formattedDateLastModified);
    }
}
