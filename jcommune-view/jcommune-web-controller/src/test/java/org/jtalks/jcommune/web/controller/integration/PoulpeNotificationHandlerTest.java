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
package org.jtalks.jcommune.web.controller.integration;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;

import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PoulpeNotificationHandlerTest {
    private static final long BRANCH_ID = 1L;
    private static final long SECTION_ID = 1L;

    @Mock
    private BranchService branchService;
    @Mock
    private SectionService sectionService;
    @Mock
    private UserService userService;

    private PoulpeNotificationHandler controller;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        controller = new PoulpeNotificationHandler(branchService, sectionService, userService);
    }

    @Test
    public void testDeleteBranch() throws NotFoundException {
        doReturn(userWithPassword("password")).when(userService).getCommonUserByUsername("admin");
        controller.deleteBranch(BRANCH_ID, "password");

        verify(branchService).deleteAllTopics(BRANCH_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfEmptyPasswordWasSentByPoulpe() throws NotFoundException {
        controller.deleteBranch(BRANCH_ID, "");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfNullPasswordWasSentByPoulpe() throws NotFoundException {
        controller.deleteBranch(BRANCH_ID, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfWrongPasswordWasSentByPoulpe() throws NotFoundException {
        doReturn(userWithPassword("correct password")).when(userService).getCommonUserByUsername("admin");
        controller.deleteBranch(BRANCH_ID, "wrong password");
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteBranchIncorrectId() throws NotFoundException {
        doReturn(userWithPassword("password")).when(userService).getCommonUserByUsername("admin");
        when(branchService.deleteAllTopics(anyLong())).thenThrow(new NotFoundException());
        controller.deleteBranch(BRANCH_ID, "password");
    }

    @Test
    public void testDeleteSection() throws NotFoundException {
        doReturn(userWithPassword("password")).when(userService).getCommonUserByUsername("admin");
        controller.deleteSection(SECTION_ID, "password");

        verify(sectionService).deleteAllTopicsInSection(SECTION_ID);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteSectionIncorrectId() throws NotFoundException {
        doReturn(userWithPassword("password")).when(userService).getCommonUserByUsername("admin");
        when(sectionService.deleteAllTopicsInSection(anyLong())).thenThrow(new NotFoundException());
        controller.deleteSection(SECTION_ID, "password");

        assertTrue(false);
    }

    @Test
    public void testDeleteComponent() throws NotFoundException {
        doReturn(userWithPassword("password")).when(userService).getCommonUserByUsername("admin");
        controller.deleteComponent("password");

        verify(sectionService).deleteAllTopicsInForum();
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteComponentFail() throws NotFoundException {
        doReturn(userWithPassword("password")).when(userService).getCommonUserByUsername("admin");
        doThrow(new NotFoundException()).when(sectionService).deleteAllTopicsInForum();

        controller.deleteComponent("password");
    }

    @Test
    public void testHandleAllExceptions() {
        Exception exception = new Exception("Some message");
        ModelAndView mav = controller.handleAllExceptions(exception);

        assertTrue(mav.getView() instanceof MappingJacksonJsonView);
        assertEquals(mav.getModel().get("errorMessage"), exception.getMessage());
    }

    private User userWithPassword(String password) {
        return new User("admin", "mail", password, "");
    }
}
