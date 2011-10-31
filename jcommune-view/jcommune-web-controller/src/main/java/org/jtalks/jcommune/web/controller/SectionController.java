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

import org.jtalks.jcommune.model.entity.Section;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.UserSessionRegistryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays to user page contains section list with related branch lists
 * and page contains branch list from the chosen section
 *
 * @author Max Malakhov
 * @author Alexandre Teterin
 */

@Controller
public final class SectionController {

    private SectionService sectionService;
    private BreadcrumbBuilder breadcrumbBuilder;

    private UserSessionRegistryImpl sessionRegistry;

    /**
     * Constructor creates MVC controller with specified SectionService
     *
     * @param sectionService    autowired object from Spring Context
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     * @param sessionRegistry   autowired object from Spring Security Context
     */
    @Autowired
    public SectionController(SectionService sectionService, BreadcrumbBuilder breadcrumbBuilder,
                             UserSessionRegistryImpl sessionRegistry) {
        this.sectionService = sectionService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * This method handles GET request and produces JSP page with all branch sections
     *
     * @return {@link ModelAndView} with view name as renderAllSection
     */
    @RequestMapping(value = "/sections", method = RequestMethod.GET)
    public ModelAndView sectionList() {
        List<User> users = null;
        if (sessionRegistry.getAllPrincipals().size() > 0) {
            users = new ArrayList<User>();
            for (Object user : sessionRegistry.getAllPrincipals()) {
                users.add((User) user);
            }
        }
        return new ModelAndView("sectionList")
                .addObject("sectionList", sectionService.getAll())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb())
                .addObject("messagesCount", sectionService.getPostsOnForumCount())
                .addObject("registeredUsersCount", sectionService.getUsersCount())
                .addObject("visitors", UserSessionRegistryImpl.getTotalActiveSessions())
                .addObject("usersRegistered", users)
                .addObject("visitorsRegistered", sessionRegistry.getAllPrincipals().size())
                .addObject("visitorsGuests", UserSessionRegistryImpl.getTotalActiveSessions() - sessionRegistry.getAllPrincipals().size());
    }

    /**
     * Action for root path. Do same as SectionController#sectionList()
     *
     * @return populated {@code ModelAndView}
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView root() {
        return sectionList();
    }

    /**
     * Displays to user a list of branches from the chosen section.
     *
     * @param sectionId section for display
     * @return {@code ModelAndView} the chosen section
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when section not found
     */
    @RequestMapping(value = "/sections/{sectionId}", method = RequestMethod.GET)
    public ModelAndView branchList(@PathVariable("sectionId") long sectionId) throws NotFoundException {
        Section section = sectionService.get(sectionId);

        return new ModelAndView("branchList")
                .addObject("section", section)
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb());
    }
}
