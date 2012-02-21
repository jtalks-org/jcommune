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
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.service.nontransactional.SecurityService;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author Vitaliy kravchenko
 * @author Kirill Afonin
 * @author Alexandre Teterin
 * @author Evgeniy Naumenko
 * @author Eugeny Batov
 */

@Controller
public class BranchController {

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
     * @param locationService   autowired object from Spring Context
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.util.BreadcrumbBuilder} entity
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
        List<Topic> topics = branch.getTopics();
        JCUser currentUser = securityService.getCurrentUser();

        Pagination pag = new Pagination(page, currentUser, topics.size(), pagingEnabled);
        List<Breadcrumb> breadcrumbs = breadcrumbBuilder.getForumBreadcrumb(branch);

        return new ModelAndView("topicList")
                .addObject("viewList", locationService.getUsersViewing(branch))
                .addObject("branch", branch)
                .addObject("topics", topics)
                .addObject("pagination", pag)
                .addObject("breadcrumbList", breadcrumbs)
                .addObject("subscribed", branch.getSubscribers().contains(currentUser));
    }

    /**
     * Displays to user a list of topic updated since last login.
     * For anonymous user this method will display topics
     * updated during last 24 hours.
     *
     * @param page    page
     * @param session http session to figure out last login
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping(value = "/topics/recent", method = RequestMethod.GET)
    public ModelAndView recentTopicsPage(@RequestParam(value = PAGE, defaultValue = "1", required = false) Integer page,
                                         HttpSession session) {

        DateTime lastLogin = (DateTime) session.getAttribute("lastlogin");
        JCUser currentUser = securityService.getCurrentUser();
        List<Topic> topics = topicService.getRecentTopics(lastLogin);
        Pagination pagination = new Pagination(page, currentUser, topics.size(), true);

        return new ModelAndView("recent")
                .addObject("topics", topics)
                .addObject("pagination", pagination);
    }

    /**
     * Displays to user a list of topics without answers(topics which has only 1 post added during topic creation).
     *
     * @param page page
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping(value = "/topics/unanswered", method = RequestMethod.GET)
    public ModelAndView unansweredTopicsPage(@RequestParam(value = PAGE, defaultValue = "1", required = false)
                                             Integer page) {
        JCUser currentUser = securityService.getCurrentUser();
        List<Topic> topics = topicService.getUnansweredTopics();
        Pagination pagination = new Pagination(page, currentUser, topics.size(), true);

        return new ModelAndView("unansweredTopics")
                .addObject("topics", topics)
                .addObject("pagination", pagination);
    }

    /**
     * Provides all branches from section with given sectionId as JSON array.
     *
     * @param sectionId id of section
     * @return branches dto array
     * @throws NotFoundException when section with given id not found
     */
    @RequestMapping(value = "/branches/json/{sectionId}", method = RequestMethod.GET)
    @ResponseBody
    public BranchDto[] getBranchesFromSection(@PathVariable long sectionId) throws NotFoundException {
        List<Branch> branches = branchService.getBranchesInSection(sectionId);
        return convertBranchesListToBranchDtoArray(branches);
    }

    /**
     * Get all existing branches as JSON array.
     *
     * @return branches dto array
     */
    @RequestMapping(value = "/branches/json", method = RequestMethod.GET)
    @ResponseBody
    public BranchDto[] getAllBranches() {
        List<Branch> branches = branchService.getAllBranches();
        return convertBranchesListToBranchDtoArray(branches);
    }

    /**
     * Converts branch list in branch dto array.
     *
     * @param branches branch list
     * @return branch dto array
     */
    private BranchDto[] convertBranchesListToBranchDtoArray(List<Branch> branches) {
        BranchDto[] branchDtoArray = new BranchDto[branches.size()];
        for (int i = 0; i < branchDtoArray.length; i++) {
            branchDtoArray[i] = new BranchDto(branches.get(i));
        }
        return branchDtoArray;
    }
}