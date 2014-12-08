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


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValue;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.SimplePage;
import org.jtalks.jcommune.service.SimplePageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.dto.SimplePageInfoContainer;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.SimplePageDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Alexander Gavrikov
 * @author Scherbakov Roman
 */

public class SimplePageControllerTest {

    private SimplePageController controller;

    @Mock
    private SimplePageService simplePageService;
    
    @Mock
    private UserService userService;

    private static final long PAGE_ID = 1L;
    private static final String NAME = "test";
    private static final String CONTENT = "test_post";
    private static final String PATH_NAME = "test";

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        controller = new SimplePageController(simplePageService, userService);
        //expectations for 2 or more tests
        JCUser currentUser = new JCUser("username", "email", "password");
        when(userService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    public void showPageTest() throws NotFoundException {
        SimplePage simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);

        //set expectations
        when(simplePageService.getPageByPathName(PATH_NAME)).thenReturn(simplePage);

        //invoke the object under test
        ModelAndView modelAndView = controller.showPage(PATH_NAME);

        //check expectations
        verify(simplePageService).getPageByPathName(PATH_NAME);

        //check result
        assertViewName(modelAndView, "simplePage");
        SimplePageDto actualSimplePage = assertAndReturnModelAttributeOfType(modelAndView, "simplePageDto", SimplePageDto.class);
        assertEqualsSimplePageAndSimplePageDto(actualSimplePage, simplePage);
    }

    @Test
    public void showEditPageTest() throws NotFoundException {
        SimplePage simplePage = new SimplePage(NAME, CONTENT, PATH_NAME);

        //set expectations
        when(simplePageService.getPageByPathName(PATH_NAME)).thenReturn(simplePage);
        

        //invoke the object under test
        ModelAndView modelAndView = controller.showEditPage(PATH_NAME);

        //check expectations
        verify(simplePageService).getPageByPathName(PATH_NAME);

        //check result
        assertViewName(modelAndView, "simplePageEditor");
        SimplePageDto actualSimplePage = assertAndReturnModelAttributeOfType(modelAndView, "simplePageDto", SimplePageDto.class);
        assertEqualsSimplePageAndSimplePageDto(actualSimplePage, simplePage);
    }

    @Test (expectedExceptions = NotFoundException.class)
    public void showEditPageFailTest() throws NotFoundException {
        doThrow(new NotFoundException()).when(simplePageService).getPageByPathName(PATH_NAME);
        controller.showEditPage(PATH_NAME);
    }

    @Test
    public void updatePageTest() throws NotFoundException {
        SimplePageDto pageDto = new SimplePageDto();
        pageDto.setId(PAGE_ID);
        pageDto.setNameText(NAME);
        pageDto.setContentText(CONTENT);
        pageDto.setPathName(PATH_NAME);
        BindingResult bindingResult = new BeanPropertyBindingResult(pageDto, "simplePageDto");
        ModelAndView mav = controller.update(pageDto, bindingResult, PATH_NAME);
        assertViewName(mav, "redirect:/pages/" + PATH_NAME);
        verify(simplePageService).updatePage(any(SimplePageInfoContainer.class));
    }

    @Test
    public void updatePageWithErrorTest() throws NotFoundException {
        //create Dto
        SimplePageDto pageDto = new SimplePageDto();
        pageDto.setId(PAGE_ID);
        pageDto.setNameText(NAME);
        pageDto.setContentText(CONTENT);
        pageDto.setPathName(PATH_NAME);

        //create Mock-object
        BeanPropertyBindingResult resultWithErrors = mock(BeanPropertyBindingResult.class);
        when(resultWithErrors.hasErrors()).thenReturn(true);
        ModelAndView mav = controller.update(pageDto, resultWithErrors, PATH_NAME);

        assertViewName(mav, "simplePageEditor");

        verify(simplePageService, never()).updatePage(any(SimplePageInfoContainer.class));
    }

    private void assertEqualsSimplePageAndSimplePageDto(SimplePageDto simplePageDto, SimplePage simplePage) {
        assertEquals(simplePageDto.getNameText(), simplePage.getName());
        assertEquals(simplePageDto.getPathName(), simplePage.getPathName());
        assertEquals(simplePageDto.getContentText(), simplePage.getContent());
    }

    @Test
    public void showNotExistingPageTest() throws NotFoundException {
        JCUser user = new JCUser("username", "email", "password");
        
        when(simplePageService.getPageByPathName(PATH_NAME)).thenThrow(new NotFoundException());
        when(userService.getCurrentUser()).thenReturn(user);
        
        ModelAndView modelAndView = controller.showPage(PATH_NAME);
        
        assertViewName(modelAndView, SimplePageController.PAGE_NOT_FOUND);
        assertModelAttributeValue(modelAndView, SimplePageController.PAGE_PATH_NAME, PATH_NAME);
        assertModelAttributeValue(modelAndView, SimplePageController.CURRENT_USER_PARAMETER, user);
    }

}
