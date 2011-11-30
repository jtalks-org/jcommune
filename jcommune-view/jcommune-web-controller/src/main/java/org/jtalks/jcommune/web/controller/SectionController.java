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
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.ForumStatisticsProvider;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Displays to user page contains section list with related branch lists
 * and page contains branch list from the chosen section
 *
 * @author Max Malakhov
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */

@Controller
public final class SectionController {

    private SecurityService securityService;
    private SectionService sectionService;
    private ForumStatisticsProvider forumStaticsProvider;
    private BreadcrumbBuilder breadcrumbBuilder;
    //todo: replace injection with method parameter
    private HttpSession session;

    /**
     * Constructor creates MVC controller with specified SectionService
     *
     * @param securityService      autowired object from Spring Context
     * @param sectionService       autowired object from Spring Context
     * @param breadcrumbBuilder    the object which provides actions on
     *                             {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     * @param forumStaticsProvider autowired object from Spring Context which provides methods for getting
     *                             forum statistic information
     * @param session              http session that will be initiated
     */
    @Autowired
    public SectionController(SecurityService securityService,
                             SectionService sectionService,
                             BreadcrumbBuilder breadcrumbBuilder,
                             ForumStatisticsProvider forumStaticsProvider,
                             HttpSession session) {
        this.securityService = securityService;
        this.sectionService = sectionService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.forumStaticsProvider = forumStaticsProvider;
        this.session = session;
    }

    /**
     * This method handles GET request and produces JSP page with all branch sections
     *
     * @return {@link ModelAndView} with view name as renderAllSection
     */
    @RequestMapping(value = {"/", "/sections"}, method = RequestMethod.GET)
    public ModelAndView sectionList() {
        // Counting the number of active users based on the number of sessions.
        // By default, the session will be initialized after controller's invocation,
        // so at the time of request processing, we can miss the session
        // if the current request is the first one for a particular user.
        // To change a default behavior we call getId() method
        // that initializes the session right now.
        // If a request from the user is not the first one this method will have no effect.
        session.getId();
        User user = securityService.getCurrentUser();
        return new ModelAndView("sectionList")
                .addObject("pageSize", Pagination.getPageSizeFor(user))
                .addObject("sectionList", sectionService.getAll())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb())
                .addObject("messagesCount", forumStaticsProvider.getPostsOnForumCount())
                .addObject("registeredUsersCount", forumStaticsProvider.getUsersCount())
                .addObject("visitors", forumStaticsProvider.getOnlineUsersCount())
                .addObject("usersRegistered", forumStaticsProvider.getOnlineRegisteredUsers())
                .addObject("visitorsRegistered", forumStaticsProvider.getOnlineRegisteredUsersCount())
                .addObject("visitorsGuests", forumStaticsProvider.getOnlineAnonymousUsersCount());
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
        User user = securityService.getCurrentUser();
        return new ModelAndView("branchList")
                .addObject("section", section)
                .addObject("pageSize", Pagination.getPageSizeFor(user))
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb());
    }
}
