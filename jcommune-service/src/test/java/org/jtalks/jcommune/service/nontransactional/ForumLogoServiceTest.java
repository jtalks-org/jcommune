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

package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCommuneProperty;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertTrue;

public class ForumLogoServiceTest {
    private static final String PROPERTY_NAME = "property";
    private static final String LOGO_MAX_SIZE = "1000";
    private JCommuneProperty logoSizeProperty = JCommuneProperty.FORUM_LOGO_MAX_SIZE;
    @Mock
    private ImageUtils imageUtils;
    @Mock
    private Base64Wrapper base64Wrapper;

    private ForumLogoService forumLogoService;


    @BeforeMethod
    public void setUp() {
        initMocks(this);
        logoSizeProperty.setName(PROPERTY_NAME);
        logoSizeProperty.setDefaultValue(LOGO_MAX_SIZE);
        logoSizeProperty.setPropertyDao(null);
        forumLogoService = new ForumLogoService(
                imageUtils,
                base64Wrapper,
                "org/jtalks/jcommune/service/jcommune-logo.jpeg",
                "org/jtalks/jcommune/service/favicon.ico",
                "org/jtalks/jcommune/service/favicon.png",
                logoSizeProperty);
    }



    @Test
    public void getDefaultAvatarShouldReturnNotEmptyAvatar() {
        byte[] logo = forumLogoService.getDefaultLogo();

        assertTrue(logo.length > 0);
    }
}
