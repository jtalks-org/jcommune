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

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.web.dto.OperationResultDto;
import org.jtalks.jcommune.web.util.JSONUtils;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Alexandre Teterin
 */
public class AvatarControllerTest {

    @Mock
    private AvatarService avatarService;
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private JSONUtils jsonUtils;

    private AvatarController avatarController;

    private final String USER_NAME = "username";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String SRC_IMG = "srcImage";

    private byte[] validAvatar = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
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
        avatarController = new AvatarController(avatarService,  userService, messageSource, jsonUtils);
    }

    @Test(dataProvider = "validDataForOperaIE")
    public void testValidUploadAvatarForOperaIE(
            MultipartFile file,
            ResponseEntity<String> expectedResponseEntity,
            String imageJSON) throws Exception {
        //set expectations
        when(avatarService.convertBytesToBase64String(validAvatar)).thenReturn(SRC_IMG);
        when(jsonUtils.prepareJSONString(Matchers.anyMap())).thenReturn(imageJSON);

        //invoke object under test
        ResponseEntity<String> actualResponseEntity = avatarController.uploadAvatar(file);

        //check expectations
        verify(avatarService).convertBytesToBase64String(validAvatar);

        //check result
        assertEquals(actualResponseEntity.getStatusCode(), expectedResponseEntity.getStatusCode());
        assertEquals(actualResponseEntity.getBody(), expectedResponseEntity.getBody());
        assertEquals(actualResponseEntity.getHeaders(), expectedResponseEntity.getHeaders());

    }

    @Test(dataProvider = "validDataForChromeFF")
    public void testValidUploadAvatarForChromeFF(byte[] avatar, Map<String, String> expectedData) throws Exception {
        //set expectations
        when(avatarService.convertBytesToBase64String(avatar)).thenReturn(SRC_IMG);

        HttpServletResponse response = new MockHttpServletResponse();

        //invoke object under test
        Map<String, String> result = avatarController.uploadAvatar(avatar, response);

        //check result
        assertEquals(result, expectedData);
    }

    @Test(dataProvider = "validDataForChromeFF")
    public void testRenderAvatarModified(byte[] avatar, Map<String, String> expectedData) throws Exception {
        JCUser user = getUser();
        user.setAvatar(avatar);
        user.setAvatarLastModificationTime(new DateTime(1000));
        when(userService.get(anyLong())).thenReturn(user);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(avatarService.getIfModifiedSineDate(anyString()))
            .thenReturn(new Date(0));
        
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        avatarController.renderAvatar(request, response, 0L);

        verify(response).setContentType("image/jpeg");
        verify(response).setContentLength(avatar.length);
        verify(response).getOutputStream();
        verify(servletOutputStream).write(avatar);
        
        verify(response).setHeader("Pragma", "public");
        verify(response).setHeader("Cache-Control", "public");
        verify(response).addHeader("Cache-Control", "must-revalidate");
        verify(response).addHeader("Cache-Control","max-age=0");
        verify(response).setHeader(eq("Expires"), anyString()); //System.currentTimeMillis() is used
        verify(response).setHeader(eq("Last-Modified"), anyString()); // depends on current timezone
    }
    
    @Test(dataProvider = "validDataForChromeFF")
    public void testRenderAvatarNotModified(
            byte[] avatar, Map<String, String> expectedData) throws Exception {
        JCUser user = getUser();
        user.setAvatar(avatar);
        user.setAvatarLastModificationTime(new DateTime(0));
        when(userService.get(anyLong())).thenReturn(user);
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(avatarService.getIfModifiedSineDate(anyString()))
            .thenReturn(new Date(1000));
        
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        avatarController.renderAvatar(request, response, 0L);

        verify(response).setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        verify(response, never()).getOutputStream();
        verify(servletOutputStream, never()).write(avatar);
        verify(response).setHeader("Pragma", "public");
        verify(response).setHeader("Cache-Control", "public");
        verify(response).addHeader("Cache-Control", "must-revalidate");
        verify(response).addHeader("Cache-Control","max-age=0");
        verify(response).setHeader(eq("Expires"), anyString()); //System.currentTimeMillis() is used
        verify(response).setHeader(eq("Last-Modified"), anyString()); // depends on current timezone
    }

    @Test(dataProvider = "testDataForGetDefaultAvatar")
    public void testGetDefaultAvatar(String srcImage) throws IOException, ImageProcessException {
        String expectedJSON = "{\"team\": \"larks\"}";
        //set expectations
        when(avatarService.getDefaultAvatar()).thenReturn(validAvatar);
        when(avatarService.convertBytesToBase64String(validAvatar)).thenReturn(srcImage);
        when(jsonUtils.prepareJSONString(Matchers.anyMap())).thenReturn(expectedJSON);

        //invoke object under test
        String actualJSON = avatarController.getDefaultAvatar();

        //check expectations
        verify(avatarService).getDefaultAvatar();
        verify(jsonUtils).prepareJSONString(Matchers.anyMap());
        //check result
        assertEquals(actualJSON, expectedJSON);
    }

    @DataProvider
    @SuppressWarnings("unused")
    private Object[][] testDataForGetDefaultAvatar() throws IOException, ImageProcessException {
        final String RESULT = "success";
        Map<String, String> map = new HashMap<String, String>();

        AvatarService avatarService = new AvatarService(
                new ImageUtils(new Base64Wrapper()),
                new Base64Wrapper(),
                StringUtils.EMPTY,
                null);
        String srcImage = avatarService.convertBytesToBase64String(validAvatar);
        map.put(RESULT, "true");
        map.put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
        map.put("srcImage", srcImage);

        return new Object[][]{
                {srcImage}
        };

    }

    @DataProvider
    @SuppressWarnings("unused")
    private Object[][] validDataForChromeFF() {

        Map<String, String> normalResponseContent = new HashMap<String, String>() {{
            put("success", "true");
            put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
            put("srcImage", SRC_IMG);
        }};

        return new Object[][]{
                {validAvatar, normalResponseContent}
        };
    }

    @DataProvider
    @SuppressWarnings("unused")
    private Object[][] validDataForOperaIE() {

        String name = "qqfile";
        MockMultipartFile file = new MockMultipartFile(name, validAvatar);

        String goodBody = "{\"srcPrefix\":\"data:image/jpeg;base64,\",\"srcImage\":\"srcImage\",\"success\":\"true\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        ResponseEntity<String> goodResponseEntity = new ResponseEntity<String>(goodBody, headers, HttpStatus.OK);

        return new Object[][]{
                {file, goodResponseEntity, goodBody},
        };
    }
    
    @Test
    public void ImageFormatExceptionShouldProduceNotSuccessOperationResultWithMessageAboutValidImageTypes() {
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message";
        String validTypes = "*.png";
        boolean expectedSuccess = false;
        //
        when(messageSource.getMessage(
                AvatarController.WRONG_FORMAT_RESOURCE_MESSAGE,
                new Object[]{validTypes},
                locale)
                ).thenReturn(expectedMessage);
        //
        OperationResultDto result = avatarController.handleImageFormatException(new ImageFormatException(validTypes), locale);
        //
        assertEquals(result.isSuccess(), expectedSuccess, "We have an exception, so we should get false value.");
        assertEquals(result.getMessage(), expectedMessage, "Result contains incorrect message.");
    }
    
    @Test
    public void testHandleImageSizeException() {
        int maxSize = 1000;
        ImageSizeException exception = new ImageSizeException(maxSize);
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message " + maxSize;
        boolean expectedSuccess = false;
        //
        when(messageSource.getMessage(
                Matchers.anyString(),
                Matchers.any(Object[].class),
                Matchers.any(Locale.class))
                ).thenReturn(expectedMessage);
        //
        OperationResultDto result = avatarController.handleImageSizeException(exception, locale);
        //
        assertEquals(result.isSuccess(), expectedSuccess, "We have an exception, so we should get false value.");
        assertEquals(result.getMessage(), expectedMessage, "Result contains incorrect message.");
    }
    
    @Test
    public void testHandleImageProcessException() {
        Locale locale = Locale.ENGLISH;//it's not matter
        String expectedMessage = "a message";
        boolean expectedSuccess = false;
        //
        when(messageSource.getMessage(
                AvatarController.COMMON_ERROR_RESOURCE_MESSAGE,
                null,
                locale)
                ).thenReturn(expectedMessage);
        //
        OperationResultDto result = avatarController.handleImageProcessException(null, locale);
        //
        assertEquals(result.isSuccess(), expectedSuccess, "We have an exception, so we should get false value.");
        assertEquals(result.getMessage(), expectedMessage, "Result contains incorrect message.");
    }

    private JCUser getUser() throws IOException {
        JCUser newUser = new JCUser(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        return newUser;
    }
}
