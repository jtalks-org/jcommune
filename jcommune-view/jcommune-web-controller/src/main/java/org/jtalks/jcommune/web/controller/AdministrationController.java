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

import org.jtalks.jcommune.model.entity.ComponentInformation;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.json.JsonResponse;
import org.jtalks.jcommune.web.dto.json.JsonResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    /**
     * Creates instance of the service
     *
     * @param componentService service to work with the forum component
     * @param messageSource    to resolve locale-dependent messages
     */
    @Autowired
    public AdministrationController(ComponentService componentService,
                                    MessageSource messageSource,
                                    BranchService branchService) {
        this.messageSource = messageSource;
        this.componentService = componentService;
        this.branchService = branchService;
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
     * Returns redirect string to previous page
     *
     * @param request Client HTTP request
     */
    private String getRedirectToPrevPage(HttpServletRequest request) {
        return "redirect:" + request.getHeader("Referer");
    }


}
