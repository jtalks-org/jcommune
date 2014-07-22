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

import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.plugin.api.PluginLoader;
import org.jtalks.jcommune.plugin.api.dto.CreateTopicBtnDto;
import org.jtalks.jcommune.plugin.api.filters.TypeFilter;
import org.jtalks.jcommune.plugin.api.core.Plugin;
import org.jtalks.jcommune.plugin.api.core.TopicPlugin;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.project;

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
    private BranchService branchService;
    private PostService postService;
    private TopicFetchService topicFetchService;
    private LastReadPostService lastReadPostService;
    private UserService userService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private LocationService locationService;
    private PluginLoader pluginLoader;
    private final PermissionEvaluator aclEvaluator;
    private final SecurityContextFacade securityContextFacade;

    /**
     * Post count in the RSS for recent posts in the branch
     */
    private static final int RECENT_POST_COUNT = 15;


    /**
     * Constructor creates MVC controller with specified BranchService
     *
     * @param branchService       for branch-related service actions
     * @param topicFetchService   for topic-related service actions
     * @param lastReadPostService service to retrieve unread posts information
     * @param userService         to get user currently logged in
     * @param breadcrumbBuilder   for creating breadcrumbs
     * @param locationService     to fetch user forum page location info
     * @param postService         to get separate posts
     * @param securityContextFacade
     */
    @Autowired
    public BranchController(BranchService branchService,
                            TopicFetchService topicFetchService,
                            LastReadPostService lastReadPostService,
                            UserService userService,
                            BreadcrumbBuilder breadcrumbBuilder,
                            LocationService locationService,
                            PostService postService,
                            PermissionEvaluator aclEvaluator,
                            SecurityContextFacade securityContextFacade,
                            PluginLoader pluginLoader) {
        this.branchService = branchService;
        this.topicFetchService = topicFetchService;
        this.lastReadPostService = lastReadPostService;
        this.userService = userService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.locationService = locationService;
        this.postService = postService;
        this.aclEvaluator = aclEvaluator;
        this.securityContextFacade = securityContextFacade;
        this.pluginLoader = pluginLoader;
    }

    /**
     * Displays to user a list of topic from the chosen branch with pagination.
     *
     * @param branchId branch for display
     * @param page     page
     * @return {@code ModelAndView} with topics list and vars for pagination
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/branches/{branchId}", method = RequestMethod.GET)
    public ModelAndView showPage(@PathVariable("branchId") long branchId,
                                 @RequestParam(value = PAGE, defaultValue = "1", required = false) String page
    ) throws NotFoundException {

        branchService.checkIfBranchExists(branchId);
        Branch branch = branchService.get(branchId);
        Page<Topic> topicsPage = topicFetchService.getTopics(branch, page);
        lastReadPostService.fillLastReadPostForTopics(topicsPage.getContent());

        JCUser currentUser = userService.getCurrentUser();
        List<Breadcrumb> breadcrumbs = breadcrumbBuilder.getForumBreadcrumb(branch);

        return new ModelAndView("topic/topicList")
                .addObject("viewList", locationService.getUsersViewing(branch))
                .addObject("branch", branch)
                .addObject("topicsPage", topicsPage)
                .addObject("breadcrumbList", breadcrumbs)
                .addObject("topicTypes", getTopicTypes(branchId))
                .addObject("subscribed", branch.getSubscribers().contains(currentUser));
    }

    /**
     * Get the list of the topic types which are allowed for the current User and specified Branch
     * @param branchId id of the branch where topic will be created
     * @return list of the topic type information objects
     */
    private List<CreateTopicBtnDto> getTopicTypes(long branchId) {
        Authentication authentication = securityContextFacade.getContext().getAuthentication();
        boolean hasTopicPermission =
                aclEvaluator.hasPermission(authentication, branchId, "BRANCH", "BranchPermission.CREATE_POSTS");
        boolean hasReviewPermission =
                aclEvaluator.hasPermission(authentication, branchId, "BRANCH", "BranchPermission.CREATE_CODE_REVIEW");

        List<CreateTopicBtnDto> topicTypes = new ArrayList<>();

        if (hasTopicPermission) {
            topicTypes.add(new CreateTopicBtnDto("new-topic-btn", "label.addtopic", "label.addtopic.tip", "/topics/new?branchId=" + branchId));
        }

        if (hasReviewPermission) {
            topicTypes.add(new CreateTopicBtnDto("new-code-review-btn", "label.addCodeReview", "label.addCodeReview.tip", "/reviews/new?branchId=" + branchId));
        }

        List<TopicPlugin> topicPlugins = getEnabledTopicPlugins();
        for (TopicPlugin topicPlugin : topicPlugins) {
            if (aclEvaluator.hasPermission(authentication, branchId, "BRANCH", topicPlugin.getCreateTopicPermission())) {
                topicTypes.add(topicPlugin.getCreateTopicBtnDto(branchId));
            }
        }

        return topicTypes;
    }

    /**
     * Gets list of enabled topic plugins
     * @return list of topic plugins
     * @see org.jtalks.jcommune.plugin.api.core.TopicPlugin
     */
    private List<TopicPlugin> getEnabledTopicPlugins() {
        List<TopicPlugin> topicPlugins = new ArrayList<>();
        List<Plugin> plugins = pluginLoader.getPlugins(new TypeFilter(TopicPlugin.class));
        for (Plugin plugin : plugins) {
            if (plugin.isEnabled()) {
                topicPlugins.add((TopicPlugin) plugin);
            }
        }
        return topicPlugins;
    }

    /**
     * Displays last messages for the branch.
     *
     * @return {@code ModelAndView} with post list and vars for pagination
     */
    @RequestMapping("/branches/{branchId}/recent")
    public ModelAndView recentBranchPostsPage(@PathVariable("branchId") long branchId) throws NotFoundException {
        branchService.checkIfBranchExists(branchId);
        Branch branch = branchService.get(branchId);

        List<Post> posts = postService.getLastPostsFor(branch, RECENT_POST_COUNT);

        return new ModelAndView("posts/recent")
                .addObject("feedTitle", branch.getName())
                .addObject("feedDescription", branch.getDescription())
                .addObject("urlSuffix", branch.prepareUrlSuffix())
                .addObject("posts", posts);

    }

    /**
     * Displays topics updated during last 24 hours.
     *
     * @param page page
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping("/topics/recent")
    public ModelAndView recentTopicsPage(
            @RequestParam(value = PAGE, defaultValue = "1", required = false) String page) {
        Page<Topic> topicsPage = topicFetchService.getRecentTopics(page);
        lastReadPostService.fillLastReadPostForTopics(topicsPage.getContent());

        return new ModelAndView("topic/recent")
                .addObject("topicsPage", topicsPage)
                .addObject("topics", topicsPage.getContent());  // for rssViewer
    }

    /**
     * Displays to user a list of topics without answers(topics which has only 1 post added during topic creation).
     *
     * @param page page
     * @return {@code ModelAndView} with topics list and vars for pagination
     */
    @RequestMapping("/topics/unanswered")
    public ModelAndView unansweredTopicsPage(@RequestParam(value = PAGE, defaultValue = "1", required = false)
                                             String page) {
        Page<Topic> topicsPage = topicFetchService.getUnansweredTopics(page);
        lastReadPostService.fillLastReadPostForTopics(topicsPage.getContent());
        return new ModelAndView("topic/unansweredTopics")
                .addObject("topicsPage", topicsPage);
    }

    /**
     * Provides all available for move topic branches from section with given sectionId as JSON array.
     *
     * @param currentTopicId id of topic that we want to move
     * @param sectionId      id of section
     * @return branches dto array
     * @throws NotFoundException when section with given id not found
     */
    @RequestMapping("/branches/json/{currentTopicId}/{sectionId}")
    @ResponseBody
    public BranchDto[] getBranchesFromSection(@PathVariable("currentTopicId") long currentTopicId,
                                              @PathVariable("sectionId") long sectionId) throws NotFoundException {

        List<Branch> branches = branchService.getAvailableBranchesInSection(sectionId, currentTopicId);
        return convertBranchesListToBranchDtoArray(branches);
    }

    /**
     * Get all available for move topic branches as JSON array.
     *
     * @param currentTopicId id of topic that we want to move
     * @return branches dto array
     */
    @RequestMapping("/branches/json/{currentTopicId}")
    @ResponseBody
    public BranchDto[] getAllBranches(@PathVariable("currentTopicId") long currentTopicId) {
        List<Branch> branches = branchService.getAllAvailableBranches(currentTopicId);
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
