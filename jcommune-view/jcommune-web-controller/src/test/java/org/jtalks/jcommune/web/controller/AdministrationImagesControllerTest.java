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

import org.jtalks.common.model.entity.Component;
import org.jtalks.common.model.entity.ComponentType;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.service.nontransactional.Base64Wrapper;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.jtalks.jcommune.service.transactional.TransactionalComponentService;
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
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Andrei Alikov
 */
public class AdministrationImagesControllerTest {

    private byte[] validImage = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
            0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
            -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
            -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
            23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
            -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
            -82, 66, 96, -126
    };
    private static final String IMAGE_BYTE_ARRAY_IN_BASE_64_STRING = "it's dummy string";

    @Mock
    ComponentService componentService;

    @Mock
    MessageSource messageSource;

    @Mock
    ImageControllerUtils logoControllerUtils;

    @Mock
    ImageControllerUtils favIconPngControllerUtils;

    @Mock
    ImageControllerUtils favIconIcoControllerUtils;

    @Mock
    ImageService iconImageService;

    //
    private AdministrationImagesController administrationController;

    @BeforeMethod
    public void init() {
        initMocks(this);

        Component component = new Component("Forum", "Cool Forum", ComponentType.FORUM);
        component.setId(42);

        administrationController = new AdministrationImagesController(componentService, logoControllerUtils,
                favIconPngControllerUtils, favIconIcoControllerUtils,
                messageSource);
    }

    @Test
    public void uploadLogoForOperaAndIEShouldReturnPreviewInResponce()
            throws IOException, ImageProcessException {
        MultipartFile file = new MockMultipartFile("qqfile", validImage);

        ResponseEntity<String> actualResponseEntity = administrationController.uploadLogo(file);

        verify(logoControllerUtils).prepareResponse(eq(file), any(HttpHeaders.class), any(HashMap.class));
    }

    @Test
    public void uploadLogoForChromeAndFFShouldReturnPreviewInResponce() throws ImageProcessException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        administrationController.uploadLogo(validImage, response);

        verify(logoControllerUtils).prepareResponse(eq(validImage), eq(response), any(HashMap.class));
    }

    @Test
    public void getDefaultLogoShouldReturnDefaultAvatarInBase64String() throws IOException, ImageProcessException {
        String expectedJSON = "{\"team\": \"larks\"}";
        when(logoControllerUtils.getDefaultImage()).thenReturn(validImage);
        when(logoControllerUtils.convertImageToIcoInString64(validImage)).thenReturn(IMAGE_BYTE_ARRAY_IN_BASE_64_STRING);
        when(logoControllerUtils.getResponceJSONString(Matchers.anyMap())).thenReturn(expectedJSON);

        String actualJSON = administrationController.getDefaultLogoInJson();

        assertEquals(actualJSON, expectedJSON);
    }

    @Test
    public void renderLogoShouldReturnModifiedLogoInResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(componentService.getComponentModificationTime()).thenReturn(new Date(1000));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(administrationController.IF_MODIFIED_SINCE_HEADER, new Date(0));

        when(logoControllerUtils.getDefaultImage()).thenReturn(validImage);

        administrationController.getForumLogo(request, response);

        assertEquals(response.getContentType(), "image/jpeg");
        assertEquals(response.getContentLength(), validImage.length);
        assertEquals(response.getContentAsByteArray(), validImage);
        checkResponse(response);
    }

    @Test
    public void renderLogoShouldNotReturnNotModifiedLogoInResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(componentService.getComponentModificationTime()).thenReturn(new Date(0));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(administrationController.IF_MODIFIED_SINCE_HEADER, new Date(1000));

        when(logoControllerUtils.getDefaultImage()).thenReturn(validImage);

        administrationController.getForumLogo(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_MODIFIED);
        assertNotSame(response.getContentAsByteArray(), validImage);
        checkResponse(response);
    }

    @Test
    public void getForumLogoShouldReturnDefaultLogoWhenLogoPropertyIsEmpty() throws ImageProcessException {
        Component forumComponent = new Component();
        forumComponent.addProperty(TransactionalComponentService.LOGO_PROPERTY, "");
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(logoControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getForumLogo(new MockHttpServletRequest(), response);

        verify(logoControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getForumLogoShouldReturnDefaultLogoWhenLogoPropertyIsNull() throws ImageProcessException {
        Component forumComponent = new Component();
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(logoControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getForumLogo(new MockHttpServletRequest(), response);

        verify(logoControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getForumLogoShouldReturnDefaultLogoWhenNoComponent() throws ImageProcessException {
        when(componentService.getComponentOfForum()).thenReturn(null);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(logoControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getForumLogo(new MockHttpServletRequest(), response);

        verify(logoControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getForumLogoShouldReturnPropertyLogoWhenPropertyExists() throws IOException, ImageProcessException {
        Component forumComponent = new Component();

        String logoProperty = "logo";
        Base64Wrapper wrapper = new Base64Wrapper();
        byte[] logoBytes = wrapper.decodeB64Bytes(logoProperty);

        forumComponent.addProperty(TransactionalComponentService.LOGO_PROPERTY, logoProperty);
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getForumLogo(new MockHttpServletRequest(), response);

        verify(logoControllerUtils, never()).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), logoBytes);
    }


    @Test
    public void getFavIconPNGShouldReturnDefaultIconWhenIconPropertyIsEmpty() throws ImageProcessException {
        Component forumComponent = new Component();
        forumComponent.addProperty(TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM, "");
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(favIconPngControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconPNG(new MockHttpServletRequest(), response);

        verify(favIconPngControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getFavIconPNGShouldReturnDefaultIconWhenIconPropertyIsNull() throws ImageProcessException {
        Component forumComponent = new Component();
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(favIconPngControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconPNG(new MockHttpServletRequest(), response);

        verify(favIconPngControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getFavIconPNGShouldReturnDefaultIconWhenNoComponent() throws ImageProcessException {
        when(componentService.getComponentOfForum()).thenReturn(null);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(favIconPngControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconPNG(new MockHttpServletRequest(), response);

        verify(favIconPngControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getFavIconPNGLogoShouldReturnPropertyIconWhenPropertyExists() throws IOException, ImageProcessException {
        Component forumComponent = new Component();

        String logoProperty = "logo";
        Base64Wrapper wrapper = new Base64Wrapper();
        byte[] logoBytes = wrapper.decodeB64Bytes(logoProperty);

        forumComponent.addProperty(TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM, logoProperty);
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconPNG(new MockHttpServletRequest(), response);

        verify(favIconPngControllerUtils, never()).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), logoBytes);
    }


    @Test
    public void getFavIconICOShouldReturnDefaultIconWhenIconPropertyIsEmpty() throws ImageProcessException {
        Component forumComponent = new Component();
        forumComponent.addProperty(TransactionalComponentService.COMPONENT_FAVICON_PNG_PARAM, "");
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(favIconIcoControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconICO(new MockHttpServletRequest(), response);

        verify(favIconIcoControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getFavIconICOShouldReturnDefaultIconWhenIconPropertyIsNull() throws ImageProcessException {
        Component forumComponent = new Component();
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(favIconIcoControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconICO(new MockHttpServletRequest(), response);

        verify(favIconIcoControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getFavIconICOShouldReturnDefaultIconWhenNoComponent() throws ImageProcessException {
        when(componentService.getComponentOfForum()).thenReturn(null);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());
        when(favIconIcoControllerUtils.getDefaultImage()).thenReturn(validImage);

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconICO(new MockHttpServletRequest(), response);

        verify(favIconIcoControllerUtils).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), validImage);
    }

    @Test
    public void getFavIconICOLogoShouldReturnPropertyIconWhenPropertyExists() throws IOException, ImageProcessException {
        Component forumComponent = new Component();

        String logoProperty = "logo";
        Base64Wrapper wrapper = new Base64Wrapper();
        byte[] logoBytes = wrapper.decodeB64Bytes(logoProperty);

        forumComponent.addProperty(TransactionalComponentService.COMPONENT_FAVICON_ICO_PARAM, logoProperty);
        when(componentService.getComponentOfForum()).thenReturn(forumComponent);
        when(componentService.getComponentModificationTime()).thenReturn(new Date());

        MockHttpServletResponse response = new MockHttpServletResponse();
        administrationController.getFavIconICO(new MockHttpServletRequest(), response);

        verify(favIconIcoControllerUtils, never()).getDefaultImage();
        assertEquals(response.getContentAsByteArray(), logoBytes);
    }


    @Test
    public void iconPngRequestShouldReturnModifiedIconInResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(componentService.getComponentModificationTime()).thenReturn(new Date(1000));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(administrationController.IF_MODIFIED_SINCE_HEADER, new Date(0));

        when(favIconPngControllerUtils.getDefaultImage()).thenReturn(validImage);

        administrationController.getFavIconPNG(request, response);

        assertEquals(response.getContentType(), "image/png");
        assertEquals(response.getContentLength(), validImage.length);
        assertEquals(response.getContentAsByteArray(), validImage);
        checkResponse(response);
    }

    @Test
    public void iconPngRequestShouldNotReturnModifiedIconInResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(componentService.getComponentModificationTime()).thenReturn(new Date(0));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(administrationController.IF_MODIFIED_SINCE_HEADER, new Date(1000));

        when(favIconPngControllerUtils.getDefaultImage()).thenReturn(validImage);

        administrationController.getFavIconPNG(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_MODIFIED);
        assertNotSame(response.getContentAsByteArray(), validImage);
        checkResponse(response);
    }

    @Test
    public void iconIcoRequestShouldReturnModifiedIconInResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(componentService.getComponentModificationTime()).thenReturn(new Date(1000));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(administrationController.IF_MODIFIED_SINCE_HEADER, new Date(0));

        when(favIconIcoControllerUtils.getDefaultImage()).thenReturn(validImage);

        administrationController.getFavIconICO(request, response);

        assertEquals(response.getContentType(), "image/x-icon");
        assertEquals(response.getContentLength(), validImage.length);
        assertEquals(response.getContentAsByteArray(), validImage);
        checkResponse(response);
    }

    @Test
    public void iconIcoRequestShouldNotReturnModifiedIconInResponse() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(componentService.getComponentModificationTime()).thenReturn(new Date(0));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(administrationController.IF_MODIFIED_SINCE_HEADER, new Date(1000));

        when(favIconIcoControllerUtils.getDefaultImage()).thenReturn(validImage);

        administrationController.getFavIconICO(request, response);

        assertEquals(response.getStatus(), HttpServletResponse.SC_NOT_MODIFIED);
        assertNotSame(response.getContentAsByteArray(), validImage);
        checkResponse(response);

    }

    private void checkResponse(MockHttpServletResponse response) {
        assertEquals(response.getHeader("Pragma"), "public");
        List<String> cacheControlHeaders = response.getHeaders("Cache-Control");
        Assert.assertTrue(cacheControlHeaders.contains("public"));
        assertNotNull(response.getHeader("Expires"));
        assertNotNull(response.getHeader("Last-Modified"));
    }

    @Test
    public void uploadIconForOperaAndIEShouldReturnPreviewInResponce()
            throws IOException, ImageProcessException {
        MultipartFile file = new MockMultipartFile("qqfile", validImage);

        administrationController.uploadFavIcon(file);

        verify(favIconPngControllerUtils).prepareResponse(eq(file), any(HttpHeaders.class), any(HashMap.class));
    }

    @Test
    public void uploadIconForChromeAndFFShouldReturnPreviewInResponce() throws ImageProcessException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        administrationController.uploadFavIcon(validImage, response);

        verify(favIconPngControllerUtils).prepareResponse(eq(validImage), eq(response), any(HashMap.class));
    }
}
