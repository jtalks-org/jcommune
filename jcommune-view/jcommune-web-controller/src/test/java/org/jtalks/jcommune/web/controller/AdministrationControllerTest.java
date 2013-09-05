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
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Locale;

import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * @author Andrei Alikov
 */
public class AdministrationControllerTest {

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

    @Mock
    BranchService branchService;

    //
    private AdministrationController administrationController;

    @BeforeMethod
    public void init() {
        initMocks(this);

        Component component = new Component("Forum", "Cool Forum", ComponentType.FORUM);
        component.setId(42);

        administrationController = new AdministrationController(componentService, messageSource, branchService);
    }

    @Test
    public void enterAdminModeShouldSetSessionAttributeAndReturnPreviousPageRedirect() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("Referer")).thenReturn(initialPage);
        HttpSession session = new MockHttpSession();
        when(request.getSession()).thenReturn(session);

        String resultUrl = administrationController.enterAdministrationMode(request);

        Boolean attr = (Boolean) session.getAttribute(AdministrationController.ADMIN_ATTRIBUTE_NAME);
        assertTrue(attr);
        assertEquals(resultUrl, "redirect:" + initialPage);
    }

    @Test
    public void exitAdminModeShouldRemoveSessionAttributeAndReturnPreviousPageRedirect() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("Referer")).thenReturn(initialPage);
        HttpSession session = new MockHttpSession();
        when(request.getSession()).thenReturn(session);

        String resultUrl = administrationController.exitAdministrationMode(request);

        Object attr = session.getAttribute(AdministrationController.ADMIN_ATTRIBUTE_NAME);

        assertNull(attr);
        assertEquals(resultUrl, "redirect:" + initialPage);
    }

    @Test
    public void validForumInformationShouldProduceSuccessResponse() {
        Component component = new Component();
        component.setId(1L);
        when(componentService.getComponentOfForum()).thenReturn(component);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "");
        ComponentInformation ci = new ComponentInformation();
        when(favIconIcoControllerUtils.getImageService()).thenReturn(iconImageService);
        JsonResponse response = administrationController.setForumInformation(ci, bindingResult, Locale.UK);

        verify(componentService).setComponentInformation(ci);
        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
    }

    @Test
    public void invalidForumInformationShouldProduceFailResponse() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "");
        bindingResult.addError(new ObjectError("name", "message"));
        JsonResponse response = administrationController.setForumInformation(new ComponentInformation(), bindingResult,
                Locale.UK);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
    }

    @Test
    public void validBranchInformationShouldProduceSuccessResponse() throws NotFoundException {
        Component component = new Component();
        component.setId(1L);
        when(componentService.getComponentOfForum()).thenReturn(component);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "");
        BranchDto branchDto = new BranchDto();
        JsonResponse response = administrationController.setBranchInformation(branchDto, bindingResult, Locale.UK);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
    }

    @Test
    public void invalidBranchInformationShouldProduceFailResponse() throws NotFoundException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "");
        bindingResult.addError(new ObjectError("name", "message"));
        BranchDto branchDto = new BranchDto();
        JsonResponse response = administrationController.setBranchInformation(branchDto, bindingResult, Locale.UK);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
    }
}
