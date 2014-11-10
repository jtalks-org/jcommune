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

import org.jtalks.common.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Controller that handles notifications from Poulpe. Main purpose of this is to support data consistency, e.g. when
 * deleting branch Poulpe can't take care of topics, proper post count, notifications and so on. That is why messages
 * are sent here. Note that in order to make things secure, Poulpe sends admin password as a parameter and we're
 * matching it with what we have in our database, if passwords are the same, then removal is allowed, otherwise an error
 * response is sent back.
 *
 * @author Vyacheslav Mishcheryakov
 * @author Evgeniy Naumenko
 */
@Controller
public class PoulpeNotificationHandler {
    private static final String ERROR_MESSAGE_PARAMETER = "errorMessage";
    /**
     * A username to find admin user in the database in order to check its password and what was sent in the delete
     * request. We use this to secure removal of sections/branches/components so that only Poulpe can do that and no one
     * else (because only Poulpe knows the admin password).
     */
    private static final String ADMIN_USERNAME = "admin";

    private final BranchService branchService;
    private final SectionService sectionService;
    private final UserService userService;

    @Autowired
    public PoulpeNotificationHandler(BranchService branchService, SectionService sectionService,
                                     UserService userService) {
        this.branchService = branchService;
        this.sectionService = sectionService;
        this.userService = userService;
    }

    /**
     * Handles notification about branch deletion. This method deletes all topics in branch. Branch itself is not
     * deleted as Poulpe can cope with it
     *
     * @param branchId      branch id
     * @param adminPassword password of admin
     * @throws NotFoundException is thrown if branch not found
     */
    @RequestMapping(value = "/branches/{branchId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteBranch(@PathVariable("branchId") long branchId,
                             @RequestParam(value = "password") String adminPassword) throws NotFoundException {
        assertAdminPasswordCorrect(adminPassword);
        branchService.deleteAllTopics(branchId);
    }

    /**
     * Handles notification about section deletion. Removes all topics from section. Sections and branches won't be
     * removed
     *
     * @param sectionId     section id
     * @param adminPassword password of admin
     * @throws NotFoundException is thrown if section not found
     */
    @RequestMapping(value = "/sections/{sectionId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteSection(@PathVariable("sectionId") long sectionId,
                              @RequestParam(value = "password") String adminPassword) throws NotFoundException {
        assertAdminPasswordCorrect(adminPassword);
        sectionService.deleteAllTopicsInSection(sectionId);
    }

    /**
     * Handles notification about component deletion. As for now it removes all the topics, leaving branches, sections
     * and components untouched.
     *
     * @param adminPassword password of admin
     * @throws NotFoundException if object for deletion has not been found
     */
    @RequestMapping(value = "/component", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteComponent(@RequestParam(value = "password") String adminPassword) throws NotFoundException {
        assertAdminPasswordCorrect(adminPassword);
        sectionService.deleteAllTopicsInForum();
    }

    /**
     * Catches all exceptions threw by any method in this controller and returns HTTP status 500 with error message in
     * response body instead.
     *
     * @param exception exception that was thrown
     * @return {@link ModelAndView} object with JSON view and error message as model
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception exception) {
        MappingJacksonJsonView jsonView = new MappingJacksonJsonView();
        ModelAndView mav = new ModelAndView(jsonView);
        mav.addObject(ERROR_MESSAGE_PARAMETER, exception.getMessage());
        return mav;
    }

    /**
     * Checks whether the admin password is not blank and it matches the admin password from database. Note that Poulpe
     * sends hash of the password to us and thus we compare it with has as well.
     *
     * @param adminPassword the password sent by Poulpe
     * @throws NotFoundException if the password sent by Poulpe is blank or does not match the one in the
     *                           database
     */
    private void assertAdminPasswordCorrect(String adminPassword) throws NotFoundException {
        checkArgument(isNotBlank(adminPassword), "No password specified while it is required");
        User admin = userService.getCommonUserByUsername(ADMIN_USERNAME);
        checkArgument(adminPassword.equals(admin.getPassword()),
                "Wrong password was specified during removal of branch/section/component.");
    }

}
