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
import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.jtalks.jcommune.service.dto.UserInfoContainer;
import org.jtalks.jcommune.web.dto.EditUserProfileDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

/**
 * @author Alexandre Teterin
 *         Date: 16.02.13
 */


public class ExternalLinkControllerTest {

    private static final long ID = 1L;
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String HINT = "hint";

    @Mock
    private ExternalLinkService service;
    @Mock
    private ComponentService componentService;
    @Mock
    BindingResult bindingResult;

    private ExternalLinkController controller;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        controller = new ExternalLinkController(service, componentService);
    }

    @Test
    public void testSaveLink() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(false);

        JsonResponse expected = controller.saveLink(createLink(), bindingResult);

        assertEquals(expected.getStatus(), JsonResponseStatus.SUCCESS);
        verify(service).saveLink(any(ExternalLink.class), any(Component.class));
    }

    @Test
    public void testFailValidationSaveLink() throws Exception {
        when(bindingResult.hasErrors()).thenReturn(true);

        JsonResponse expected = controller.saveLink(createLink(), bindingResult);

        assertEquals(expected.getStatus(), JsonResponseStatus.FAIL);
        verify(service, never()).saveLink(any(ExternalLink.class), any(Component.class));
    }

    @Test
    public void testDeleteLink() throws Exception {
        boolean expectedResult = true;
        when(service.deleteLink(eq(ID), any(Component.class))).thenReturn(expectedResult);
        JsonResponse expected = controller.deleteLink(ID);
        assertEquals(expected.getStatus(), JsonResponseStatus.SUCCESS);
        verify(service).deleteLink(eq(ID), any(Component.class));
    }

    private ExternalLink createLink() {
        ExternalLink link = new ExternalLink(URL, TITLE, HINT);
        link.setId(ID);
        return link;
    }
}
