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

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

import java.util.List;

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.TopicFetchService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
    private TopicFetchService topicFetchService;
    private LastReadPostService lastReadPostService;
    private UserService userService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private LocationService locationService;


    /**
     * Constructor creates MVC controller with specified BranchService
     *
     * @param branchService       for branch-related service actions
     * @param topicFetchService   for topic-related service actions
     * @param lastReadPostService service to retrieve unread posts information
     * @param userService         to get user currently logged in
     * @param locationService     to fetch user forum page location info
     * @param breadcrumbBuilder   for creating breadcrumbs
     */
    @Autowired
    public BranchController(BranchService branchService,
                            TopicFetchService topicFetchService,
                            LastReadPostService lastReadPostService,
                            UserService userService,
                            BreadcrumbBuilder breadcrumbBuilder,
                            LocationService locationService) {
        this.branchService = branchService;
        this.topicFetchService = topicFetchService;
        this.lastReadPostService = lastReadPostService;
        this.userService = userService;
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
        Page<Topic> topicsPage = topicFetchService.getTopics(branch, page, pagingEnabled);
        lastReadPostService.fillLastReadPostForTopics(topicsPage.getContent());

        JCUser currentUser = userService.getCurrentUser();
        List<Breadcrumb> breadcrumbs = breadcrumbBuilder.getForumBreadcrumb(branch);

        return new ModelAndView("topicList")
                .addObject("viewList", locationService.getUsersViewing(branch))
                .addObject("branch", branch)
                .addObject(PAGING_ENABLED, pagingEnabled)
                .addObject("topicsPage", topicsPage)
                .addObject("breadcrumbList", breadcrumbs)
                .addObject("subscribed", branch.getSubscribers().contains(currentUser));
    }

    /**
     * Displays topics updated during last 24 hours.
     *
     * @param page page
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping("/topics/recent")
    public ModelAndView recentTopicsPage(
            @RequestParam(value = PAGE, defaultValue = "1", required = false) Integer page) {
        Page<Topic> topicsPage = topicFetchService.getRecentTopics(page);
        lastReadPostService.fillLastReadPostForTopics(topicsPage.getContent());

        return new ModelAndView("recent")
                .addObject("topicsPage", topicsPage)
                .addObject("topics", topicsPage.getContent())  // for rssViewer
                .addObject(PAGING_ENABLED, true);
    }

    /**
     * Displays to user a list of topics without answers(topics which has only 1 post added during topic creation).
     *
     * @param page page
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping("/topics/unanswered")
    public ModelAndView unansweredTopicsPage(@RequestParam(value = PAGE, defaultValue = "1", required = false)
                                             Integer page) {
        Page<Topic> topicsPage = topicFetchService.getUnansweredTopics(page);
        lastReadPostService.fillLastReadPostForTopics(topicsPage.getContent());
        return new ModelAndView("unansweredTopics")
                .addObject("topicsPage", topicsPage)
                .addObject(PAGING_ENABLED, true);
    }

    /**
     * Provides all branches from section with given sectionId as JSON array.
     *
     * @param sectionId id of section
     * @return branches dto array
     * @throws NotFoundException when section with given id not found
     */
    @RequestMapping("/branches/json/{sectionId}")
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
    @RequestMapping("/branches/json")
    @ResponseBody
    public BranchDto[] getAllBranches() {
        List<Branch> branches = branchService.getAllBranches();
        return convertBranchesListToBranchDtoArray(branches);
    }

    /**
     * Converts branch list into branch dto array.
     *
     * @param branches branch list
     * @return branch dto array
     */
    private BranchDto[] convertBranchesListToBranchDtoArray(List<Branch> branches) {
        List<BranchDto> dtos = project(branches, BranchDto.class,
                on(Branch.class).getId(),
                on(Branch.class).getName());
        return dtos.toArray(new BranchDto[dtos.size()]);
    }
}
