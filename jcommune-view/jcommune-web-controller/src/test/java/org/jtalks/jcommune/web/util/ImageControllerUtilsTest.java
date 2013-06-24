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

import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

/**
 *
 */
public class ImageControllerUtilsTest {
    ImageControllerUtils imageControllerUtils;

    @Mock
    private ImageService imageService;
    @Mock
    private JSONUtils jsonUtils;

    private static final String IMAGE_BYTE_ARRAY_IN_BASE_64_STRING = "it's dummy string";

    private byte[] validAvatar = new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };


    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        imageControllerUtils = new ImageControllerUtils(imageService, jsonUtils);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void uploadAvatarForOperaAndIEShouldReturnPreviewInResponce()
            throws IOException, ImageProcessException {
        MultipartFile file = new MockMultipartFile("qqfile", validAvatar);
        String expectedBody = "{\"srcPrefix\":\"data:image/jpeg;base64,\",\"srcImage\":\"srcImage\",\"success\":\"true\"}";
        when(imageService.preProcessAndEncodeInString64(validAvatar)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        when(imageControllerUtils.getResponceJSONString(Matchers.anyMap())).thenReturn(expectedBody);
        Map<String, String> responseContent = new HashMap<String, String>();
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_HTML);

        ResponseEntity<String> actualResponseEntity = imageControllerUtils.prepareResponse(file, responseHeaders, responseContent);

        verify(imageService).validateImageFormat(file);
        verify(imageService).validateImageSize(file.getBytes());
        assertEquals(actualResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(actualResponseEntity.getBody(), expectedBody);
        HttpHeaders headers = actualResponseEntity.getHeaders();
        assertEquals(headers.getContentType(), MediaType.TEXT_HTML);
    }

    @Test
    public void uploadAvatarForChromeAndFFShouldReturnPreviewInResponce() throws ImageProcessException {
        when(imageService.preProcessAndEncodeInString64(validAvatar)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        when(imageService.getHtmlSrcImagePrefix()).thenReturn("jpeg");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Map<String, String> responseContent = new HashMap<String, String>();

        imageControllerUtils.prepareResponse(validAvatar, response, responseContent);

        verify(imageService).validateImageFormat(validAvatar);
        verify(imageService).validateImageSize(validAvatar);
        assertEquals(responseContent.get(imageControllerUtils.STATUS), "SUCCESS");
        assertEquals(responseContent.get(imageControllerUtils.SRC_PREFIX), "jpeg");
        assertEquals(responseContent.get(imageControllerUtils.SRC_IMAGE), IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
    }

    @Test
    public void validImageShouldGenerateValidNormalResponse() throws ImageProcessException {
        when(imageService.preProcessAndEncodeInString64(validAvatar)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        when(imageService.getHtmlSrcImagePrefix()).thenReturn("jpeg");
        Map<String, String> responseContent = new HashMap<String, String>();

        imageControllerUtils.prepareNormalResponse(validAvatar, responseContent);

        assertEquals(responseContent.get(imageControllerUtils.STATUS), "SUCCESS");
        assertEquals(responseContent.get(imageControllerUtils.SRC_PREFIX), "jpeg");
        assertEquals(responseContent.get(imageControllerUtils.SRC_IMAGE), IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
    }

    @Test
    public void getDefaultImageShouldReturnDefaultImageOfImageService() {
        when(imageService.getDefaultImage()).thenReturn(validAvatar);

        byte[] image = imageControllerUtils.getDefaultImage();

        assertEquals(validAvatar, image);
    }
}
