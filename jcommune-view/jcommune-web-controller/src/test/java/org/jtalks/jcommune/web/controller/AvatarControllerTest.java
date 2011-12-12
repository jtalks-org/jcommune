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

import org.jtalks.jcommune.service.AvatarService;
import org.jtalks.jcommune.service.util.ImageUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 */
public class AvatarControllerTest {

    private AvatarService avatarService;
    private AvatarController avatarController;
    private final String SRC_IMG = "srcImage";

    @BeforeMethod
    public void setUp() throws Exception {
        avatarService = mock(AvatarService.class);
        avatarController = new AvatarController(avatarService);

    }

    @Test
    public void testUploadAvatarFromIE() throws Exception {

    }

    @Test(dataProvider = "normal-byte-ServletResponse-provider")
    public void testNormalUploadAvatar(byte[] avatar, Map<String, String> expectedData) throws Exception {
        //set expectations
        when(avatarService.convertAvatarToString(avatar)).thenReturn(SRC_IMG);

        HttpServletResponse response = new MockHttpServletResponse();

        //invoke object under test
        Map<String, String> result = avatarController.uploadAvatar(avatar, response);

        //check result
        assertEquals(result, expectedData);
    }

    @Test(dataProvider = "error-byte-ServletResponse-provider")
    public void testErrorUploadAvatar(byte[] avatar, Map<String, String> expectedData) throws Exception {
        //set expectations
        when(avatarService.convertAvatarToString(avatar)).thenThrow(new IOException());

        HttpServletResponse response = new MockHttpServletResponse();

        //invoke object under test
        Map<String, String> result = avatarController.uploadAvatar(avatar, response);

        //check result
        assertEquals(result, expectedData);
    }

    @DataProvider(name = "normal-byte-ServletResponse-provider")
    private Object[][] validByteServletResponseData() {
        byte[] validAvatar = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0,
                0, 0, 4, 0, 0, 0, 4, 1, 0, 0, 0, 0, -127, -118, -93, -45, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 1,
                -118, 0, 0, 1, -118, 1, 51, -105, 48, 88, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 37, 0, 0,
                -128, -125, 0, 0, -7, -1, 0, 0, -128, -23, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0,
                23, 111, -110, 95, -59, 70, 0, 0, 0, 22, 73, 68, 65, 84, 120, -38, 98, -40, -49, -60, -64, -92,
                -64, -60, 0, 0, 0, 0, -1, -1, 3, 0, 5, -71, 0, -26, -35, -7, 32, 96, 0, 0, 0, 0, 73, 69, 78, 68,
                -82, 66, 96, -126
        };


        Map<String, String> normalResponseContent = new HashMap<String, String>() {{
            put("success", "true");
            put("srcPrefix", ImageUtils.HTML_SRC_TAG_PREFIX);
            put("srcImage", SRC_IMG);
        }};

        return new Object[][]{
                {validAvatar, normalResponseContent},
        };
    }

    @DataProvider(name = "error-byte-ServletResponse-provider")
    private Object[][] invalidByteServletResponseData() {

        byte[] invalidAvatar = null;

        Map<String, String> errorResponseContent = new HashMap<String, String>() {{
            put("success", "false");
        }};


        return new Object[][]{
                {invalidAvatar, errorResponseContent}
        };


    }
}
