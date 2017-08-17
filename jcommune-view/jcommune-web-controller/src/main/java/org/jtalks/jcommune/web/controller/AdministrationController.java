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

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.validation.ValidationError;
import org.jtalks.common.validation.ValidationException;
import org.jtalks.jcommune.model.dto.*;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponse;
import org.jtalks.jcommune.plugin.api.web.dto.json.JsonResponseStatus;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.security.PermissionManager;
import org.jtalks.jcommune.web.dto.*;
import org.jtalks.jcommune.web.listeners.SessionSetupListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Andrei Alikov
 *         Controller for processing forum administration related requests
 *         such as setting up Forum title, description, logo and fav icon
 *         Some methods in controller are directly used from test classes,
 *         and some of them are not @SuppressWarnings("unused") is used to
 *         disable IDE warnings about methods without direct access.
 */
@SuppressWarnings("unused")
@Controller
public class AdministrationController {

    /**
     * Session's marker attribute name for Administration mode
     */
    static final String ADMIN_ATTRIBUTE_NAME = "adminMode";
    private static final String ACCESS_DENIED_MESSAGE = "access.denied";
    private static final int GROUP_USER_PAGE_SIZE = 20;

    private final ComponentService componentService;
    private final GroupService groupService;
    private final UserService userService;
    private final MessageSource messageSource;
    private final BranchService branchService;
    private final PermissionManager permissionManager;
    private final SpamProtectionService spamProtectionService;

    @Autowired
    public AdministrationController(ComponentService componentService,
                                    MessageSource messageSource,
                                    BranchService branchService,
                                    PermissionManager permissionManager,
                                    GroupService groupService,
                                    SpamProtectionService spamProtectionService,
                                    UserService userService) {
        this.messageSource = messageSource;
        this.componentService = componentService;
        this.branchService = branchService;
        this.permissionManager = permissionManager;
        this.groupService = groupService;
        this.spamProtectionService = spamProtectionService;
        this.userService = userService;
    }

    /**
     * Change mode to Administrator mode in which user can edit
     * forum parameters - external links, banners, logo, title, etc.
     *
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/enter", method = RequestMethod.GET)
    public String enterAdministrationMode(HttpServletRequest request) {
        if (componentService.getComponentOfForum() != null) {
            checkForAdminPermissions();
        }
        request.getSession().setAttribute(ADMIN_ATTRIBUTE_NAME, true);

        return getRedirectToPrevPage(request);
    }

    /**
     * Return back from Administrator mode to Normal mode
     *
     * @param request Client request
     * @return redirect back to previous page
     */
    @RequestMapping(value = "/admin/exit", method = RequestMethod.GET)
    public String exitAdministrationMode(HttpServletRequest request) {
        request.getSession().removeAttribute(ADMIN_ATTRIBUTE_NAME);

        return getRedirectToPrevPage(request);
    }

    /**
     * Handler for request of updating Administration information
     *
     * @param componentInformation new forum information
     * @param result               form validation result
     */
    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse setForumInformation(@Valid @RequestBody ComponentInformation componentInformation,
                                            BindingResult result, Locale locale) {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }

        componentInformation.setId(componentService.getComponentOfForum().getId());

        try {
            componentService.setComponentInformation(componentInformation);
            // SessionSetupListener read session timeout property once on application startup and
            // keeps it in memory, so when property is updated we need to read it again.
            SessionSetupListener.resetSessionTimeoutProperty();
        } catch (AccessDeniedException e) {
            String errorMessage = messageSource.getMessage(ACCESS_DENIED_MESSAGE, null, locale);
            return new JsonResponse(JsonResponseStatus.FAIL, errorMessage);
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS, null);
    }

    /**
     * Handler for request of updating Administration information
     *
     * @param result               form validation result
     */
    @RequestMapping(value = "/branch/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse setBranchInformation(@Valid @RequestBody BranchDto branchDto,
                                            BindingResult result, Locale locale) throws NotFoundException {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }

        long forumId = componentService.getComponentOfForum().getId();

        try {
            branchService.changeBranchInfo(forumId, branchDto.getId(), branchDto.getName(), branchDto.getDescription());
        } catch (AccessDeniedException e) {
            String errorMessage = messageSource.getMessage(ACCESS_DENIED_MESSAGE, null, locale);
            return new JsonResponse(JsonResponseStatus.FAIL, errorMessage);
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS, null);
    }

    @RequestMapping(value = "/branch/new", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse createNewBranch(@Valid @RequestBody BranchDto branchDto, 
                                            BindingResult result, Locale locale) throws NotFoundException {
        if (result.hasErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }

        long forumId = componentService.getComponentOfForum().getId();

        try {
            branchService.createNewBranch(forumId, branchDto.getSectionId(), branchDto.getName(), branchDto.getDescription());
        } catch (AccessDeniedException e) {
            String errorMessage = messageSource.getMessage(ACCESS_DENIED_MESSAGE, null, locale);
            return new JsonResponse(JsonResponseStatus.FAIL, errorMessage);
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS, null);
    }

    /**
     * Displays to user a list of branch permissions.
     * @param branchId id of the branch
     *
     */
    @RequestMapping(value = "/branch/permissions/{branchId}", method = RequestMethod.GET)
    public ModelAndView showBranchPermissions(@PathVariable("branchId") long branchId) throws NotFoundException {
        long forumId = componentService.getComponentOfForum().getId();
        GroupsPermissions permissions = branchService.getPermissionsFor(forumId, branchId);
        Branch branch = branchService.getBranchOfComponent(forumId, branchId);
        return new ModelAndView("branchPermissions")
                .addObject("branch", branch)
                .addObject("permissions", permissions);
    }

    /**
     * Process permission information request
     * @param permissionInfo information about permission for which data was requested
     * @return DTO with two lists for already selected groups and still available groups
     */
    @RequestMapping(value = "/branch/permissions/json", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse getGroupsForBranchPermission(@RequestBody BranchPermissionDto permissionInfo) {
        long forumId = componentService.getComponentOfForum().getId();
        PermissionGroupsDto permission = new PermissionGroupsDto();

        JtalksPermission branchPermission = permissionManager.findBranchPermissionByMask(permissionInfo.getPermissionMask());
        List<Group> selectedGroups;
        try {
            selectedGroups = branchService.getPermissionGroupsFor(forumId, permissionInfo.getBranchId(),
                    permissionInfo.isAllowed(), branchPermission);
        } catch (NotFoundException e) {
            return new JsonResponse(JsonResponseStatus.FAIL, null);
        }
        List<GroupDto> alreadySelected = GroupDto.convertToGroupDtoList(selectedGroups, GroupDto.BY_NAME_COMPARATOR);

        List<Group> availableGroups = permissionManager.getAllGroupsWithoutExcluded(selectedGroups, branchPermission);
        List<GroupDto> available = GroupDto.convertToGroupDtoList(availableGroups, GroupDto.BY_NAME_COMPARATOR);

        permission.setSelectedGroups(alreadySelected);
        permission.setAvailableGroups(available);

        return new JsonResponse(JsonResponseStatus.SUCCESS, permission);
    }

    /**
     * Process change branch permission request
     * @param permissionInfo information about permission which will be changed
     * @return "success" or "fail" response status in JSON format
     */
    @RequestMapping(value = "/branch/permissions/edit", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse editBranchPermissions(@RequestBody BranchPermissionDto permissionInfo) {
        long forumId = componentService.getComponentOfForum().getId();
        JtalksPermission branchPermission =  permissionManager.findBranchPermissionByMask(permissionInfo.getPermissionMask());

        List<Group> newlyAddedGroups = permissionManager.getGroupsByIds(permissionInfo.getNewlyAddedGroupIds());
        List<Group> removedGroups = permissionManager.getGroupsByIds(permissionInfo.getRemovedGroupIds());

        PermissionChanges changes = new PermissionChanges(branchPermission, newlyAddedGroups, removedGroups);
        try {
            branchService.changeBranchPermissions(forumId, permissionInfo.getBranchId(), permissionInfo.isAllowed(),changes);
        } catch (NotFoundException e) {
            return new JsonResponse(JsonResponseStatus.FAIL);
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }
    /**
     * Display to user list of groups with count of users.
     */
    @RequestMapping(value = "/group/list", method = RequestMethod.GET)
    public ModelAndView showGroupsWithUsers() {
        checkForAdminPermissions();
        List<GroupAdministrationDto> groupAdministrationDtos = groupService.getGroupNamesWithCountOfUsers();
        return new ModelAndView("groupAdministration").addObject("groups", groupAdministrationDtos);
    }

    /**
     * Register {@link org.jtalks.common.model.entity.Group} from populated in form {@link GroupAdministrationDto}.
     *
     * @param groupDto {@link GroupAdministrationDto} populated in form
     * @return JsonResponse with JsonResponseStatus. SUCCESS if registration successful or FAIL if failed
     */
    @RequestMapping(value = "/group", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse createNewGroup(@Valid @RequestBody GroupAdministrationDto groupDto, BindingResult result, Locale locale) throws org.jtalks.common.service.exceptions.NotFoundException {
        return saveOrUpdateGroup(groupDto, result, locale);
    }

    @RequestMapping(value = "/group/{groupId}", method = RequestMethod.PUT)
    @ResponseBody
    public JsonResponse editGroup(@Valid @RequestBody GroupAdministrationDto groupDto,
                                  BindingResult result,
                                  @PathVariable("groupId") Long groupId,
                                  Locale locale) throws org.jtalks.common.service.exceptions.NotFoundException {
        return saveOrUpdateGroup(groupDto, result, locale);
    }

    @RequestMapping(value = "/group/{groupId}", method = RequestMethod.GET)
    public ModelAndView getGroupUsers(@PathVariable("groupId") long groupId,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) int page)
            throws org.jtalks.common.service.exceptions.NotFoundException {
        checkForAdminPermissions();
        GroupDto groupDto = new GroupDto(groupService.get(groupId));
        Page<UserDto> pagedGroupUsers = getPagedGroupUsers(groupId, page);
        return new ModelAndView("groupUserList")
                .addObject("group", groupDto)
                .addObject("groupUsersPage", pagedGroupUsers);
    }

    @RequestMapping(value = "/ajax/group/{groupId}", method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse ajaxGetGroupUsers(@PathVariable("groupId") long groupId,
                                          @RequestParam(value = "page", defaultValue = "1", required = false) int page)
            throws org.jtalks.common.service.exceptions.NotFoundException {
        Page<UserDto> pagedGroupUsers = getPagedGroupUsers(groupId, page);
        return new JsonResponse(JsonResponseStatus.SUCCESS, pagedGroupUsers);
    }

    @RequestMapping(value = "/group/{groupId}", method = RequestMethod.DELETE)
    @ResponseBody
    public JsonResponse deleteGroup(@PathVariable("groupId") Long groupId) throws org.jtalks.common.service.exceptions.NotFoundException {
        checkForAdminPermissions();
        Group group = groupService.get(groupId);
        groupService.deleteGroup(group);
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    @RequestMapping(value = "/spamprotection", method = RequestMethod.GET)
    public ModelAndView getSpamProtectionPage(){
        checkForAdminPermissions();
        List<SpamRuleDto> ruleDtos = SpamRuleDto.fromEntities(spamProtectionService.getAllRules());
        return new ModelAndView("spamProtection").addObject("rules", ruleDtos);
    }

    @RequestMapping(value = "/user", params = {"notInGroupId", "pattern"}, method = RequestMethod.GET)
    @ResponseBody
    public JsonResponse findUserNotInGroup(@RequestParam("notInGroupId") long notInGroupId,
                                           @RequestParam("pattern") String pattern) {
        List<UserDto> users = userService.findByUsernameOrEmailNotInGroup(pattern, notInGroupId, 10);
        JsonResponse response = new JsonResponse(JsonResponseStatus.SUCCESS);
        response.setResult(users);
        return response;
    }

    /**
     * Returns redirect string to previous page
     *
     * @param request Client HTTP request
     */
    private String getRedirectToPrevPage(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }

    /**
     * Check if currently logged user has permissions for administrative
     * functions for forum
     */
    private void checkForAdminPermissions() {
        long forumId = componentService.getComponentOfForum().getId();
        componentService.checkPermissionsForComponent(forumId);
    }

    private JsonResponse saveOrUpdateGroup(@Valid @RequestBody GroupAdministrationDto groupDto, BindingResult result, Locale locale) throws org.jtalks.common.service.exceptions.NotFoundException {
        checkForAdminPermissions();
        if (result.hasFieldErrors() || result.hasGlobalErrors()) {
            return new JsonResponse(JsonResponseStatus.FAIL, result.getAllErrors());
        }
        try {
            groupService.saveOrUpdate(groupDto);
        } catch (ValidationException e) {
            return new JsonResponse(JsonResponseStatus.FAIL, convertErrors(e.getErrors(), result.getObjectName(), locale));
        }
        return new JsonResponse(JsonResponseStatus.SUCCESS);
    }

    private List<ObjectError> convertErrors(Set<ValidationError> errors, String objectName, Locale locale) {
        List<ObjectError> objectErrors = new ArrayList<>();
        for (ValidationError error : errors) {
            objectErrors.add(new FieldError(objectName,
                                            error.getFieldName(),
                                            messageSource.getMessage(error.getErrorMessageCode(), null, locale)));

        }
        return objectErrors;
    }

    private Page<UserDto> getPagedGroupUsers(long groupId, int page)
            throws org.jtalks.common.service.exceptions.NotFoundException {
        checkForAdminPermissions();
        GroupDto groupDto = new GroupDto(groupService.get(groupId));
        PageRequest pageRequest = new PageRequest(page, GROUP_USER_PAGE_SIZE);
        return groupService.getPagedGroupUsers(groupId, pageRequest);
    }
}
