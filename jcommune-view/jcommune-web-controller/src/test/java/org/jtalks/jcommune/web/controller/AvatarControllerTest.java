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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.ImageFormatException;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.exceptions.ImageSizeException;
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.service.nontransactional.ImageUtils;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
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
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 */
public class AvatarControllerTest {

    @Mock
    private AvatarService avatarService;
    @Mock
    private SecurityService securityService;
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    ImageUtils imageUtils;
    private AvatarController avatarController;

    private final String USER_NAME = "username";
    private final String FIRST_NAME = "first name";
    private final String LAST_NAME = "last name";
    private final String EMAIL = "mail@mail.com";
    private final String PASSWORD = "password";
    private final String SRC_IMG = "srcImage";

    private Locale locale = Locale.ENGLISH;
    private final String message = "message";


    private byte[] validAvatar = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };

    private MockMultipartFile file;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        avatarController = new AvatarController(avatarService, securityService, userService, messageSource, imageUtils);
    }

    @Test(dataProvider = "validDataForOperaIE")
    public void testValidUploadAvatarForOperaIE(Map<String, MultipartFile> fileMap,
                                                ResponseEntity<String> expectedResponseEntity) throws Exception {
        //setUp
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);

        //set expectations
        when(request.getFileMap()).thenReturn(fileMap);
        when(avatarService.convertBytesToBase64String(validAvatar)).thenReturn(SRC_IMG);

        //invoke object under test
        ResponseEntity<String> actualResponseEntity = avatarController.uploadAvatar(request, locale);

        //check expectations
        verify(request).getFileMap();
        verify(avatarService).convertBytesToBase64String(validAvatar);

        //check result
        assertEquals(actualResponseEntity.getStatusCode(), expectedResponseEntity.getStatusCode());
        assertEquals(actualResponseEntity.getBody(), expectedResponseEntity.getBody());
        assertEquals(actualResponseEntity.getHeaders(), expectedResponseEntity.getHeaders());

    }

    //TODO Must throw expectedExceptions = ImageProcessException.class
    @Test(dataProvider = "invalidDataForOperaIE")
    public void testErrorUploadAvatarDueNonImageDataForOperaIE(Map<String, MultipartFile> fileMap,
                                                               ResponseEntity<String> expectedResponseEntity)
            throws Exception {
        //setUp
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);

        //set expectations
        when(request.getFileMap()).thenReturn(fileMap);
        when(avatarService.convertBytesToBase64String(validAvatar)).thenThrow(new ImageProcessException());
        when(messageSource.getMessage(
                eq("avatar.500.common.error"), Matchers.<Object[]>any(), Matchers.<Locale>any())).thenReturn(message);

        //invoke object under test
        ResponseEntity<String> actualResponseEntity = avatarController.uploadAvatar(request, locale);

        //check expectations
        verify(request).getFileMap();
        verify(avatarService).convertBytesToBase64String(validAvatar);

        //check result
        assertEquals(actualResponseEntity.getStatusCode(), expectedResponseEntity.getStatusCode());
        assertEquals(actualResponseEntity.getBody(), expectedResponseEntity.getBody());
        assertEquals(actualResponseEntity.getHeaders(), expectedResponseEntity.getHeaders());

    }

    //TODO Must throw expectedExceptions = ImageFormatException.class
    @Test(dataProvider = "invalidDataForOperaIE")
    public void testErrorUploadAvatarDueInvalidImageFormatForOperaIE(Map<String, MultipartFile> fileMap,
                                                                     ResponseEntity<String> expectedResponseEntity)
            throws Exception {
        //setUp
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);

        //set expectations
        when(request.getFileMap()).thenReturn(fileMap);
        doThrow(new ImageFormatException()).when(avatarService).validateAvatarFormat(file);
        when(messageSource.getMessage(
                eq("image.wrong.format"), Matchers.<Object[]>any(), Matchers.<Locale>any())).thenReturn(message);

        //invoke objects under test
        ResponseEntity<String> actualResponseEntity = avatarController.uploadAvatar(request, locale);

        //check expectation
        verify(request).getFileMap();
        verify(avatarService).validateAvatarFormat(file);

        //check result
        assertEquals(actualResponseEntity.getStatusCode(), expectedResponseEntity.getStatusCode());
        assertEquals(actualResponseEntity.getBody(), expectedResponseEntity.getBody());
        assertEquals(actualResponseEntity.getHeaders(), expectedResponseEntity.getHeaders());

    }

    //TODO Must throw expectedExceptions = ImageSizeException.class
    @Test(dataProvider = "invalidDataForOperaIE")
    public void testErrorUploadAvatarDueInvalidImageSizeForOperaIE(Map<String, MultipartFile> fileMap,
                                                                   ResponseEntity<String> expectedResponseEntity)
            throws Exception {
        //setUp
        DefaultMultipartHttpServletRequest request = mock(DefaultMultipartHttpServletRequest.class);
        byte[] bytes = file.getBytes();

        //set expectations
        when(request.getFileMap()).thenReturn(fileMap);
        doThrow(new ImageSizeException()).when(avatarService).validateAvatarSize(bytes);
        when(messageSource.getMessage(
                eq("image.wrong.size" + " " + AvatarService.MAX_SIZE), Matchers.<Object[]>any(), Matchers.<Locale>any())).thenReturn(message);

        //invoke objects under test
        ResponseEntity<String> actualResponseEntity = avatarController.uploadAvatar(request, locale);

        //check expectation
        verify(request).getFileMap();
        verify(avatarService).validateAvatarSize(bytes);

        //check result
        assertEquals(actualResponseEntity.getStatusCode(), expectedResponseEntity.getStatusCode());
        assertEquals(actualResponseEntity.getBody(), expectedResponseEntity.getBody());
        assertEquals(actualResponseEntity.getHeaders(), expectedResponseEntity.getHeaders());

    }

    @Test(dataProvider = "validDataForChromeFF")
    public void testValidUploadAvatarXHR(byte[] avatar, Map<String, String> expectedData) throws Exception {
        //set expectations
        when(avatarService.convertBytesToBase64String(avatar)).thenReturn(SRC_IMG);

        HttpServletResponse response = new MockHttpServletResponse();

        //invoke object under test
        Map<String, String> result = avatarController.uploadAvatar(avatar, response, locale);

        //check result
        assertEquals(result, expectedData);
    }

    @Test(dataProvider = "invalidDataGeneralCaseForChromeFF")
    public void testErrorUploadAvatarDueInvalidDataForChromeFF(byte[] bytes, Map<String, String> expectedData)
            throws Exception {
        //setUp
        HttpServletResponse response = new MockHttpServletResponse();

        //set expectations
        when(avatarService.convertBytesToBase64String(bytes)).thenThrow(new ImageProcessException());

        //invoke object under test
        Map<String, String> result = avatarController.uploadAvatar(bytes, response, locale);

        //check expectations
        verify(avatarService).convertBytesToBase64String(bytes);

        //check result
        assertEquals(result, expectedData);
    }

    //TODO Must throw expectedExceptions = ImageSizeException.class
    @Test(dataProvider = "invalidDataCustomCaseForChromeFF")
    public void testErrorUploadAvatarDueInvalidImageSizeForChromeFF(byte[] bytes, Map<String, String> expectedData)
            throws Exception {
        //setUp
        HttpServletResponse response = new MockHttpServletResponse();

        //set expectations
        doThrow(new ImageSizeException()).when(avatarService).validateAvatarSize(bytes);
        when(messageSource.getMessage(
                eq("image.wrong.size" + " "
                        + AvatarService.MAX_SIZE), Matchers.<Object[]>any(), Matchers.<Locale>any())).thenReturn(message);

        //invoke objects under test
        Map<String, String> result = avatarController.uploadAvatar(bytes, response, locale);

        //check expectation
        verify(avatarService).validateAvatarSize(bytes);
        verify(messageSource).getMessage(eq("image.wrong.size" + " "
                + AvatarService.MAX_SIZE), Matchers.<Object[]>any(), Matchers.<Locale>any());

        //check result
        assertEquals(result, expectedData);


    }

    @Test
    public void testRemoveAvatar() throws IOException {
        JCUser user = getUser();
        when(securityService.getCurrentUser()).thenReturn(user);

        ModelAndView mav = avatarController.removeAvatarFromCurrentUser();

        assertViewName(mav, "editProfile");
        verify(securityService).getCurrentUser();
        verify(avatarService).getDefaultAvatar();
    }

    @Test(dataProvider = "validDataForChromeFF")
    public void testRenderAvatar(byte[] avatar, Map<String, String> expectedData) throws Exception {
        JCUser user = getUser();
        user.setAvatar(avatar);
        when(userService.getByUsername(Matchers.<String>any())).thenReturn(user);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream servletOutputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        avatarController.renderAvatar(response, "");

        verify(response).setContentType("image/jpeg");
        verify(response).setContentLength(avatar.length);
        verify(response).getOutputStream();
        verify(servletOutputStream).write(avatar);
    }

    @DataProvider
    private Object[][] validDataForChromeFF() {

        Map<String, String> normalResponseContent = new HashMap<String, String>() {{
            put("success", "true");
            put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
            put("srcImage", SRC_IMG);
        }};

        return new Object[][]{
                {validAvatar, normalResponseContent},
        };
    }

    @DataProvider
    private Object[][] invalidDataGeneralCaseForChromeFF() {

        Map<String, String> errorResponseContent = new HashMap<String, String>() {{
            put("success", "false");
        }};

        return new Object[][]{
                {validAvatar, errorResponseContent}
        };
    }

    @DataProvider
    private Object[][] invalidDataCustomCaseForChromeFF() {

        Map<String, String> errorResponseContent = new HashMap<String, String>() {{
            put(message, message);
            put("success", "false");
        }};

        return new Object[][]{
                {validAvatar, errorResponseContent}
        };
    }

    @DataProvider
    private Object[][] validDataForOperaIE() {

        String name = "name";
        MockMultipartFile file = new MockMultipartFile(name, validAvatar);
        Map<String, MultipartFile> fileMap = new HashMap<String, MultipartFile>(1);
        fileMap.put(name, file);

        String goodBody = "{\"srcPrefix\":\"data:image/jpeg;base64,\",\"srcImage\":\"srcImage\",\"success\":\"true\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        ResponseEntity<String> goodResponseEntity = new ResponseEntity<String>(goodBody, headers, HttpStatus.OK);

        return new Object[][]{
                {fileMap, goodResponseEntity},
        };
    }

    @DataProvider
    private Object[][] invalidDataForOperaIE() {
        String name = "name";
        file = new MockMultipartFile(name, validAvatar);
        Map<String, MultipartFile> fileMap = new HashMap<String, MultipartFile>(1);
        HttpHeaders headers = new HttpHeaders();
        fileMap.put(name, file);
        headers.setContentType(MediaType.TEXT_HTML);
        String errorBody = "{\"message\":\"message\",\"success\":\"false\"}";
        ResponseEntity<String> errorResponseEntity = new ResponseEntity<String>(errorBody,
                headers,
                HttpStatus.INTERNAL_SERVER_ERROR);

        return new Object[][]{
                {fileMap, errorResponseEntity}
        };
    }

    private JCUser getUser() throws IOException {
        JCUser newUser = new JCUser(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        return newUser;
    }

    private JCUser getUserWithoutAvatar() throws IOException {
        JCUser newUser = new JCUser(USER_NAME, EMAIL, PASSWORD);
        newUser.setFirstName(FIRST_NAME);
        newUser.setLastName(LAST_NAME);
        newUser.setAvatar(null);
        return newUser;
    }


}
