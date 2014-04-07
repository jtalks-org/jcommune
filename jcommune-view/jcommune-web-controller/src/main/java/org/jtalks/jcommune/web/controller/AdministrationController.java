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
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.PermissionManager;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.GroupDto;
import org.jtalks.jcommune.web.dto.PermissionGroupRequestDto;
import org.jtalks.jcommune.web.dto.PermissionGroupsDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Andrei Alikov
 *         Controller for processing forum administration related requests
 *         such as setting up Forum title, description, logo and fav icon
 */
@Controller
public class AdministrationController {

    /**
     * Session's marker attribute name for Administration mode
     */
    public static final String ADMIN_ATTRIBUTE_NAME = "adminMode";
    private static final String ACCESS_DENIED_MESSAGE = "access.denied";

    private final ComponentService componentService;
    private final MessageSource messageSource;
    private final BranchService branchService;
    private final PermissionManager permissionManager;

    /**
     * Creates instance of the service
     *
     * @param componentService service to work with the forum component
     * @param messageSource    to resolve locale-dependent messages
     * @param permissionManager
     */
    @Autowired
    public AdministrationController(ComponentService componentService,
                                    MessageSource messageSource,
                                    BranchService branchService,
                                    PermissionManager permissionManager) {
        this.messageSource = messageSource;
        this.componentService = componentService;
        this.branchService = branchService;
        this.permissionManager = permissionManager;
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
            long componentId = componentService.getComponentOfForum().getId();
            componentService.checkPermissionsForComponent(componentId);
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
        GroupsPermissions<BranchPermission> permissions = branchService.getPermissionsFor(forumId, branchId);
        Branch branch = branchService.get(branchId);
        return new ModelAndView("branchPermissions")
                .addObject("branch", branch)
                .addObject("permissions", permissions);
    }

    @RequestMapping(value = "/branch/permissions/json", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse getGroupsForBranchPermission(@RequestBody PermissionGroupRequestDto permissionInfo) {
        long forumId = componentService.getComponentOfForum().getId();
        PermissionGroupsDto permission = new PermissionGroupsDto();

        try {
            GroupsPermissions<BranchPermission> permissions =
                    branchService.getPermissionsFor(forumId, permissionInfo.getBranchId());

            BranchPermission branchPermission = BranchPermission.findByMask(permissionInfo.getPermissionMask());
            List<Group> selected = permissionInfo.isAllowed()? permissions.getAllowed(branchPermission) :
                                                               permissions.getRestricted(branchPermission);

            List<String> alreadySelectedGroupNames = new ArrayList<String>();
            List<GroupDto> alreadySelected = new ArrayList<GroupDto>();
            for (Group group: selected) {
                alreadySelected.add(new GroupDto(group.getId(), group.getName()));
                alreadySelectedGroupNames.add(group.getName());
            }

            List<GroupDto> remaining = new ArrayList<GroupDto>();
            List<Group> allGroups = permissionManager.getAllGroups();
            for (Group group : allGroups) {
                if (!alreadySelectedGroupNames.contains(group.getName())) {
                    remaining.add(new GroupDto(group.getId(), group.getName()));
                }
            }

            permission.setSelectedGroups(alreadySelected);
            permission.setRemainingGroups(remaining);

        } catch (NotFoundException e) {
            return new JsonResponse(JsonResponseStatus.FAIL, null);
        }

        return new JsonResponse(JsonResponseStatus.SUCCESS, permission);
    }

    /**
     * Returns redirect string to previous page
     *
     * @param request Client HTTP request
     */
    private String getRedirectToPrevPage(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }


}
