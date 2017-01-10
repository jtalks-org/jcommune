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
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.dto.GroupAdministrationDto;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.model.dto.UserDto;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.GroupService;
import org.jtalks.jcommune.service.nontransactional.ImageService;
import org.jtalks.jcommune.service.security.PermissionManager;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.BranchPermissionDto;
import org.jtalks.jcommune.web.dto.GroupDto;
import org.jtalks.jcommune.web.dto.PermissionGroupsDto;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.web.util.ImageControllerUtils;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertTrue;

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

    @Mock
    PermissionManager permissionManager;
    @Mock
    GroupService groupService;


    private MockMvc mockMvc;

    //
    private AdministrationController administrationController;

    @BeforeMethod
    public void init() {
        initMocks(this);

        administrationController = new AdministrationController(componentService, messageSource, branchService, permissionManager,groupService);
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
        Component component = setupComponentMock();

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
        Component component = setupComponentMock();

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

    @Test
    public void showBranchPermissionsShouldReturnModelAndViewWithBranchAndPermissionsInformation()
            throws Exception {
        long branchId = 42;
        Component component = setupComponentMock();

        GroupsPermissions expectedPermissions = new GroupsPermissions();
        Branch expectedBranch = new Branch("name", "description");
        when(branchService.getBranchOfComponent(component.getId(), branchId)).thenReturn(expectedBranch);
        doReturn(expectedPermissions).when(branchService).getPermissionsFor(component.getId(), branchId);

        mockMvc = MockMvcBuilders.standaloneSetup(administrationController).build();
        this.mockMvc.perform(get("/branch/permissions/42").accept(MediaType.TEXT_HTML))
                .andExpect(model().attribute("branch", expectedBranch))
                .andExpect(model().attribute("permissions", expectedPermissions));
    }

    @Test
    public void getGroupsForBranchPermissionShouldReturnFailIfBranchWasNotFound() throws Exception {
        Component component = setupComponentMock();

        BranchPermission targetPermission = BranchPermission.CREATE_POSTS;
        BranchPermissionDto dto = createBranchPermissionDto(targetPermission);
        when(permissionManager.findBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);
        when(branchService.getPermissionGroupsFor(component.getId(), dto.getBranchId(), dto.isAllowed(), targetPermission))
                .thenThrow(new NotFoundException());

        JsonResponse jsonResponse = administrationController.getGroupsForBranchPermission(dto);

        assertEquals(jsonResponse.getStatus(), JsonResponseStatus.FAIL);
    }

    @Test
    public void getGroupsForBranchPermissionShouldReturnSuccessIfBranchExistsAndPermissionHasNoGroups()
            throws Exception {
        Component component = setupComponentMock();

        BranchPermission targetPermission = BranchPermission.CREATE_POSTS;
        BranchPermissionDto dto = createBranchPermissionDto(targetPermission);
        when(branchService.getPermissionGroupsFor(component.getId(), dto.getBranchId(), dto.isAllowed(), targetPermission))
                .thenReturn(Collections.EMPTY_LIST);
        when(permissionManager.findBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);
        when(permissionManager.getAllGroupsWithoutExcluded(anyList(), eq(targetPermission))).thenReturn(Collections.EMPTY_LIST);


        JsonResponse jsonResponse = administrationController.getGroupsForBranchPermission(dto);

        assertEquals(jsonResponse.getStatus(), JsonResponseStatus.SUCCESS);
    }

    @Test
    public void getGroupsForBranchPermissionShouldReturnSuccessIfBranchExists() throws Exception {
        Component component = setupComponentMock();

        BranchPermission targetPermission = BranchPermission.CREATE_POSTS;
        BranchPermissionDto dto = createBranchPermissionDto(targetPermission);

        List<Group> selectedGroupList = Arrays.asList(new Group("1"), new Group("2"), new Group("3"));
        when(branchService.getPermissionGroupsFor(component.getId(), dto.getBranchId(), dto.isAllowed(), targetPermission))
                .thenReturn(selectedGroupList);

        List<Group> allGroupList = Arrays.asList(new Group("4"), new Group("5"), new Group("6"));
        when(permissionManager.getAllGroupsWithoutExcluded(selectedGroupList, targetPermission)).thenReturn(allGroupList);
        when(permissionManager.findBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);

        JsonResponse jsonResponse = administrationController.getGroupsForBranchPermission(dto);

        assertEquals(jsonResponse.getStatus(), JsonResponseStatus.SUCCESS);
        assertTrue(jsonResponse.getResult() instanceof PermissionGroupsDto);
        PermissionGroupsDto result = (PermissionGroupsDto)jsonResponse.getResult();
        assertEquals(result.getAvailableGroups().size(), 3);
        assertEquals(result.getSelectedGroups().size(), 3);
    }

    @Test
    public void editBranchPermissionsShouldReturnSuccessIfBranchExist() throws Exception {
        Component component = setupComponentMock();

        BranchPermission targetPermission = BranchPermission.CREATE_POSTS;
        BranchPermissionDto dto = createBranchPermissionDto(targetPermission);

        dto.setNewlyAddedGroupIds(Arrays.asList(1L, 2L));
        dto.setRemovedGroupIds(Arrays.asList(1L, 2L));
        when(permissionManager.findBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);

        JsonResponse response = administrationController.editBranchPermissions(dto);

        assertEquals(response.getStatus(), JsonResponseStatus.SUCCESS);
        verify(branchService).changeBranchPermissions(anyLong(),
                eq(dto.getBranchId()), eq(dto.isAllowed()), any(PermissionChanges.class));
    }

    @Test
    public void editBranchPermissionsShouldReturnFailIfBranchNotFound() throws Exception {
        Component component = setupComponentMock();

        BranchPermission targetPermission = BranchPermission.CREATE_POSTS;
        BranchPermissionDto dto = createBranchPermissionDto(targetPermission);

        dto.setNewlyAddedGroupIds(Arrays.asList(1L, 2L));
        dto.setRemovedGroupIds(Arrays.asList(1L, 2L));

        when(permissionManager.findBranchPermissionByMask(targetPermission.getMask())).thenReturn(targetPermission);
        doThrow(new NotFoundException()).when(branchService).changeBranchPermissions(anyLong(),
                eq(dto.getBranchId()), eq(dto.isAllowed()), any(PermissionChanges.class));

        JsonResponse response = administrationController.editBranchPermissions(dto);

        assertEquals(response.getStatus(), JsonResponseStatus.FAIL);
    }

    private Component setupComponentMock() {
        Component component = new Component();
        component.setId(1L);
        when(componentService.getComponentOfForum()).thenReturn(component);
        return component;
    }

    private BranchPermissionDto createBranchPermissionDto(BranchPermission targetPermission) {
        BranchPermissionDto dto = new BranchPermissionDto();
        dto.setAllowed(true);
        dto.setBranchId(42L);
        dto.setPermissionMask(targetPermission.getMask());
        return dto;
    }

    @Test
    public void groupAdministrationPageShouldContainListOfGroups() throws Exception {
        setupComponentMock();
        List<GroupAdministrationDto> expected = new ArrayList<>();
        expected.add(new GroupAdministrationDto("group",0));
        expected.add(new GroupAdministrationDto("group1",0));
        expected.add(new GroupAdministrationDto("group2",0));
        mockMvc = MockMvcBuilders.standaloneSetup(administrationController).build();
        when(groupService.getGroupNamesWithCountOfUsers()).thenReturn(expected);
        this.mockMvc.perform(get("/group/list").accept(MediaType.TEXT_HTML))
                .andExpect(model().attribute("groups", expected));
    }

    @Test
    public void groupAdministrationPageShouldContainListOfUsersInGroups() throws Exception {
        setupComponentMock();
        Group expectedGroup = new Group("Group name");
        expectedGroup.setId(1L);
        List<UserDto> expectedGroupUsers = new ArrayList<>();
        expectedGroupUsers.add(new UserDto("user1", "user1@mail.ru", 1L));
        expectedGroupUsers.add(new UserDto("user2", "user2@mail.ru", 2L));
        expectedGroupUsers.add(new UserDto("user3", "user3@mail.ru", 3L));
        mockMvc = MockMvcBuilders.standaloneSetup(administrationController).build();
        when(groupService.getGroupUsers(expectedGroup.getId(), 20)).thenReturn(expectedGroupUsers);
        when(groupService.get(expectedGroup.getId())).thenReturn(expectedGroup);
        ResultActions perform = this.mockMvc.perform(get("/group/1").accept(MediaType.TEXT_HTML));
        GroupDto groupDto = (GroupDto) perform.andReturn().getModelAndView().getModel().get("group");
        assertEquals(groupDto.getId(), expectedGroup.getId());
        assertEquals(groupDto.getName(), expectedGroup.getName());
        assertEquals(groupDto.getUsers(), expectedGroupUsers);
    }
}