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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;


/**
 * @author Alexandre Teterin
 * @author Anuar_Nurmakanov
 */
public class AvatarControllerTest {
    private static final String USER_NAME = "username";
    private static final String FIRST_NAME = "first name";
    private static final String LAST_NAME = "last name";
    private static final String EMAIL = "mail@mail.com";
    private static final String PASSWORD = "password";
    private static final String IMAGE_BYTE_ARRAY_IN_BASE_64_STRING = "it's dummy string";
    //
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private ImageControllerUtils imageControllerUtils;

    //
    private AvatarController avatarController;

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
        avatarController = new AvatarController(userService, imageControllerUtils, messageSource);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void uploadAvatarForOperaAndIEShouldReturnPreviewInResponce() 
            throws IOException, ImageProcessException {
        MultipartFile file = new MockMultipartFile("qqfile", validAvatar);

        ResponseEntity<String> actualResponseEntity = avatarController.uploadAvatar(file);

        verify(imageControllerUtils).prepareResponse(eq(file), any(HttpHeaders.class), any(HashMap.class));
    }

    @Test
    public void uploadAvatarForChromeAndFFShouldReturnPreviewInResponce() throws ImageProcessException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        avatarController.uploadAvatar(validAvatar, response);

        verify(imageControllerUtils).prepareResponse(eq(validAvatar), eq(response), any(HashMap.class));
    }
    
    @Test
    public void renderAvatarShouldReturnModifiedAvatarInResponse() throws IOException, NotFoundException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        JCUser user = getUser();
        user.setAvatar(validAvatar);
        user.setAvatarLastModificationTime(new DateTime(1000));
        when(userService.get(anyLong())).thenReturn(user);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(avatarController.IF_MODIFIED_SINCE_HEADER, new Date(0));

        avatarController.renderAvatar(request, response, 0L);

        assertEquals(response.getContentType(), "image/jpeg");
        assertEquals(response.getContentLength(), validAvatar.length);
        assertEquals(response.getContentAsByteArray(), validAvatar);
        assertEquals(response.getHeader("Pragma"), "public");
        List<String> cacheControlHeaders = response.getHeaders("Cache-Control");
        assertTrue(cacheControlHeaders.contains("public"));
        assertTrue(cacheControlHeaders.contains("must-revalidate"));
        assertTrue(cacheControlHeaders.contains("max-age=0"));
        assertNotNull(response.getHeader("Expires"));//System.currentTimeMillis() is used
        assertNotNull(response.getHeader("Last-Modified"));// depends on current timezone
    }
    
    @Test
    public void renderAvatarShouldNotReturnNotModifiedAvatarInResponse() throws IOException, NotFoundException {
        JCUser user = getUser();
        user.setAvatar(validAvatar);
        user.setAvatarLastModificationTime(new DateTime(0));
        when(userService.get(anyLong())).thenReturn(user);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(avatarController.IF_MODIFIED_SINCE_HEADER, new Date(1000));


        avatarController.renderAvatar(request, response, 0L);

        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_MODIFIED);
        assertNotSame(response.getContentAsByteArray(), validAvatar);
        assertEquals(response.getHeader("Pragma"), "public");
        List<String> cacheControlHeaders = response.getHeaders("Cache-Control");
        assertTrue(cacheControlHeaders.contains("public"));
        assertTrue(cacheControlHeaders.contains("must-revalidate"));
        assertTrue(cacheControlHeaders.contains("max-age=0"));
        assertNotNull(response.getHeader("Expires"));//System.currentTimeMillis() is used
        assertNotNull(response.getHeader("Last-Modified"));// depends on current timezone
    }
    
    private JCUser getUser() {
        JCUser newUser = new JCUser(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        return newUser;
    }

    @SuppressWarnings("unchecked")
    public void getDefaultAvatarShouldReturnDefaultAvatarInBase64String() throws IOException, ImageProcessException {
        String expectedJSON = "{\"team\": \"larks\"}";
        when(imageControllerUtils.getDefaultImage()).thenReturn(validAvatar);
        when(imageControllerUtils.convertImageToIcoInString64(validAvatar)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        when(imageControllerUtils.getResponceJSONString(Matchers.anyMap())).thenReturn(expectedJSON);

        String actualJSON = avatarController.getDefaultAvatar();

        assertEquals(actualJSON, expectedJSON);
    }
}
