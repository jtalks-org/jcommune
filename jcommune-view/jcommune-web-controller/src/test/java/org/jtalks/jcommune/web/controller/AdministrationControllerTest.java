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
import org.jtalks.jcommune.service.nontransactional.AvatarService;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.jtalks.jcommune.web.util.JSONUtils;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Alikov
 */
public class AdministrationControllerTest {

    //
    private AdministrationController administrationController;

    @BeforeMethod
    public void init() {
        ComponentService componentService = mock(ComponentService.class);
        AvatarService avatarService = mock(AvatarService.class);
        ImageControllerUtils imageControllerUtils = mock(ImageControllerUtils.class);
        Component component = new Component("Forum", "Cool Forum", ComponentType.FORUM);
        component.setId(42);

        administrationController = new AdministrationController(componentService, imageControllerUtils);
    }

    @Test
    public void enterAdminModeShouldReturnPreviousPageRedirect() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("Referer")).thenReturn(initialPage);
        when(request.getSession()).thenReturn(new MockHttpSession());

        String resultUrl = administrationController.enterAdministrationMode(request);

        assertEquals(resultUrl, "redirect:" + initialPage);
    }

    @Test
    public void exitAdminModeShouldReturnPreviousPageRedirect() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("Referer")).thenReturn(initialPage);
        when(request.getSession()).thenReturn(new MockHttpSession());

        String resultUrl = administrationController.exitAdministrationMode(request);

        assertEquals(resultUrl, "redirect:" + initialPage);
    }
}
