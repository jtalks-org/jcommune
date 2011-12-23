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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.LocationService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Vitaliy kravchenko
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 */

@Controller
public final class BranchController {

    public static final String PAGE = "page";
    public static final String PAGING_ENABLED = "pagingEnabled";
    private BranchService branchService;
    private TopicService topicService;
    private SecurityService securityService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private LocationService locationService;

    /**
     * Constructor creates MVC controller with specified BranchService
     *
     * @param branchService     autowired object from Spring Context
     * @param topicService      autowired object from Spring Context
     * @param securityService   autowired object from Spring Context
     * @param locationService autowired object from Spring Context
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     */
    @Autowired
    public BranchController(BranchService branchService,
                            TopicService topicService,
                            SecurityService securityService,
                            BreadcrumbBuilder breadcrumbBuilder,
                            LocationService locationService) {
        this.branchService = branchService;
        this.topicService = topicService;
        this.securityService = securityService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.locationService = locationService;
    }

    /**
     * Displays to user a list of topic from the chosen branch with pagination.
     *
     * @param branchId      branch for display
     * @param page          page
     * @param pagingEnabled number of posts on the page
     * @return {@code ModelAndView} with topics list and vars for pagination
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET)
    public ModelAndView showPage(@PathVariable("branchId") long branchId,
                                 @RequestParam(value = PAGE, defaultValue = "1", required = false) Integer page,
                                 @RequestParam(value = PAGING_ENABLED, defaultValue = "true",
                                         required = false) Boolean pagingEnabled
    ) throws NotFoundException {

        Branch branch = branchService.get(branchId);
        List<Topic> topics = topicService.getTopicsInBranch(branchId);
        User currentUser = securityService.getCurrentUser();

        Pagination pag = new Pagination(page, currentUser, topics.size(), pagingEnabled);
        List<Breadcrumb> breadcrumbs = breadcrumbBuilder.getForumBreadcrumb(branch);

        List<String> viewList = locationService.getUsersViewing(branch);
      
        return new ModelAndView("topicList")
                .addObject("viewList", viewList)
                .addObject("branch", branch)
                .addObject("topics", topics)
                .addObject("pagination", pag)
                .addObject("breadcrumbList", breadcrumbs);
    }

    /**
     * Displays to user a list of topic updated since last login.
     * For anonymous user this method will display topics
     * updated during last 24 hours.
     *
     * @param page    page
     * @param session bound http session
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping(value = "/topics/recent", method = RequestMethod.GET)
    public ModelAndView recentTopicsPage(@RequestParam(value = PAGE, defaultValue = "1", required = false) Integer page,
                                         HttpSession session) {

        DateTime lastLogin = (DateTime) session.getAttribute("lastlogin");
        User currentUser = securityService.getCurrentUser();
        List<Topic> topics = topicService.getRecentTopics(lastLogin);
        Pagination pagination = new Pagination(page, currentUser, topics.size(), true);
        
        return new ModelAndView("recent")
                .addObject("topics", topics)
                .addObject("pagination", pagination);
    }

}