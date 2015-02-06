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
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.plugin.api.web.dto.json.FailJsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseReason;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Locale;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

public class ImageUploadControllerTest {
    private ImageUploadController imageUploadController;

    @Mock
    MessageSource messageSource;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        imageUploadController = new ImageUploadController(messageSource);
    }

    @Test
    public void imageFormatExceptionShouldProduceNotSuccessOperationResultWithMessageAboutValidImageTypes() {
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message";
        String validTypes = "*.png";
        //
        when(messageSource.getMessage(
                imageUploadController.WRONG_FORMAT_RESOURCE_MESSAGE,
                new Object[]{validTypes},
                locale)
        ).thenReturn(expectedMessage);

        FailJsonResponse result = imageUploadController.handleImageFormatException(new ImageFormatException(validTypes), locale);

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "We have an exception, so we should get false value.");
        assertEquals(result.getReason(), JsonResponseReason.VALIDATION, "Failture reason should be validation");
        assertEquals(result.getResult(), expectedMessage, "Result contains incorrect message.");
    }

    @Test
    public void handleImageSizeExceptionShouldReturnValidationErrorAndErrorMessage() {
        int maxSize = 1000;
        ImageSizeException exception = new ImageSizeException(maxSize);
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message " + maxSize;
        //
        when(messageSource.getMessage(
                Matchers.anyString(),
                Matchers.any(Object[].class),
                Matchers.any(Locale.class))
        ).thenReturn(expectedMessage);

        FailJsonResponse result = imageUploadController.handleImageSizeException(exception, locale);

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "We have an exception, so we should get false value.");
        assertEquals(result.getReason(), JsonResponseReason.VALIDATION, "Failture reason should be validation");
        assertEquals(result.getResult(), expectedMessage, "Result contains incorrect message.");
    }

    @Test
    public void handleImageProcessExceptionShouldReturnInternalServerErrorAndErrorMessage() {
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message";
        //
        when(messageSource.getMessage(
                imageUploadController.COMMON_ERROR_RESOURCE_MESSAGE,
                null,
                locale)
        ).thenReturn(expectedMessage);

        FailJsonResponse result = imageUploadController.handleImageProcessException(null, locale);

        assertEquals(result.getStatus(), JsonResponseStatus.FAIL, "We have an exception, so we should get false value.");
        assertEquals(result.getReason(), JsonResponseReason.INTERNAL_SERVER_ERROR, "Failture reason should be validation");
        assertEquals(result.getResult(), expectedMessage, "Result contains incorrect message.");
    }

    @Test
    public void testGetIfModifiedSinceDate() {
        long currentMillis = System.currentTimeMillis();
        long currentTimeIgnoreMillis = (currentMillis / 1000) * 1000;
        Date date = new Date(currentTimeIgnoreMillis);
        String dateAsString = DateFormatUtils.format(date,
                ImageUploadController.HTTP_HEADER_DATETIME_PATTERN,
                Locale.US);

        Date result = imageUploadController.getIfModifiedSineDate(dateAsString);

        assertEquals(result.getTime(), date.getTime());
    }

    @Test
    public void testGetIfModifiedSinceDateNullHeader() {
        Date result = imageUploadController.getIfModifiedSineDate(null);

        assertEquals(result, new Date(0));
    }
}
