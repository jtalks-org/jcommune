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
package org.jtalks.jcommune.web.interceptors;

import org.jtalks.jcommune.service.exceptions.ImageProcessException;
import org.jtalks.jcommune.web.controller.AdministrationController;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author
 */
public class ForumLogoInterceptorTest {
    private final static String LOGO_PROPERTY = "forumLogo";

    @Mock
    private AdministrationController administrationController;

    private ForumLogoInterceptor forumLogoInterceptor;

    @BeforeMethod
    public void init() {
        initMocks(this);

        forumLogoInterceptor = new ForumLogoInterceptor(administrationController);
    }

    @Test
    public void testPostHandleNormal() throws ImageProcessException {
        ModelAndView mav = new ModelAndView("view");
        when(administrationController.getForumLogo()).thenReturn("my logo");
        forumLogoInterceptor.postHandle(null, null, null, mav);

        String forumLogo = assertAndReturnModelAttributeOfType(mav, LOGO_PROPERTY, String.class);

        assertEquals(forumLogo, "my logo");
    }

    @Test
    public void testPostHandleMavIsNull() {
        forumLogoInterceptor.postHandle(null, null, null, null);
    }

    @Test
    public void testPostHandleRedirectRequest() {
        ModelAndView mav = new ModelAndView("redirect:/somewhere");
        forumLogoInterceptor.postHandle(null, null, null, mav);

        assertNull(mav.getModel().get(LOGO_PROPERTY));
    }
}
