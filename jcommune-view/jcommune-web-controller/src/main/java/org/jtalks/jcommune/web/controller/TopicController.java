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

import org.jtalks.common.security.SecurityService;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

/**
 * Serves topic management web requests
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Teterin Alexandre
 * @author Max Malakhov
 * @author Evgeniy Naumenko
 * @author Eugeny Batov
 * @see Topic
 */
@Controller
public class TopicController {

    public static final String TOPIC_ID = "topicId";
    public static final String BRANCH_ID = "branchId";
    public static final String BREADCRUMB_LIST = "breadcrumbList";
    private static final String PAGING_ENABLED = "pagingEnabled";

    private TopicService topicService;
    private BranchService branchService;
    private LastReadPostService lastReadPostService;
    private SecurityService securityService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private LocationService locationService;
    private SessionRegistry sessionRegistry;
    private PollService pollService;

    /**
     * This method turns the trim binder on. Trim binder
     * removes leading and trailing spaces from the submitted fields.
     * So, it ensures, that all validations will be applied to
     * trimmed field values only.
     *
     * @param binder Binder object to be injected
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * @param topicService        the object which provides actions on {@link Topic} entity
     * @param branchService       the object which provides actions on  {@link Branch} entity
     * @param lastReadPostService to perform post-related actions
     * @param locationService     to track user location on forum (what page he is viewing now)
     * @param sessionRegistry     to obtain list of users currently online
     * @param securityService     to determine the current user logged in
     * @param breadcrumbBuilder   to create Breadcrumbs for pages
     * @param pollService         to create a poll and vote in a poll
     */
    @Autowired
    public TopicController(TopicService topicService,
                           BranchService branchService,
                           LastReadPostService lastReadPostService,
                           SecurityService securityService,
                           BreadcrumbBuilder breadcrumbBuilder,
                           LocationService locationService,
                           SessionRegistry sessionRegistry,
                           PollService pollService) {
        this.topicService = topicService;
        this.branchService = branchService;
        this.lastReadPostService = lastReadPostService;
        this.securityService = securityService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.locationService = locationService;
        this.sessionRegistry = sessionRegistry;
        this.pollService = pollService;
    }

    /**
     * Shows page with form for new topic
     *
     * @param branchId {@link Branch} branch, where topic will be created
     * @return {@code ModelAndView} object with "newTopic" view, new {@link TopicDto} and branch id
     * @throws NotFoundException when branch was not found
     */
    @RequestMapping(value = "/topics/new", method = RequestMethod.GET)
    public ModelAndView showNewTopicPage(@RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        return new ModelAndView("newTopic")
                .addObject("topicDto", new TopicDto())
                .addObject("branchId", branchId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getNewTopicBreadcrumb(branch));
    }

    /**
     * Create topic from data entered in form
     *
     * @param topicDto object with data from form
     * @param result   {@link BindingResult} validation result
     * @param branchId branch, where topic will be created
     * @return {@code ModelAndView} object which will be redirect to forum.html
     * @throws NotFoundException when branch not found
     * todo: move logic to service
     */
    @RequestMapping(value = "/topics/new", method = RequestMethod.POST)
    public ModelAndView createTopic(@Valid @ModelAttribute TopicDto topicDto,
                                    BindingResult result,
                                    @RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        if (result.hasErrors()) {
            return new ModelAndView("newTopic")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(branch));
        }

        Topic createdTopic;
        createdTopic = topicService.createTopic(topicDto.getTopicName(), topicDto.getBodyText(), branchId);

        if (topicDto.hasPoll()) {
            Poll poll = topicDto.preparePollFromTopicDto();
            poll.setTopic(createdTopic);
            pollService.createPoll(poll);
        }

        lastReadPostService.markTopicAsRead(createdTopic);
        return new ModelAndView("redirect:/topics/" + createdTopic.getId());
    }

    /**
     * Delete topic
     *
     * @param topicId topic id, this is the topic which contains the first post which should be deleted
     * @return redirect to branch page
     * @throws NotFoundException when topic not found
     */
    @RequestMapping(value = "/topics/{topicId}", method = RequestMethod.DELETE)
    public ModelAndView deleteTopic(@PathVariable(TOPIC_ID) Long topicId) throws NotFoundException {
        Topic topic = topicService.get(topicId);
        topicService.deleteTopic(topicId);
        return new ModelAndView("redirect:/branches/" + topic.getBranch().getId());
    }

    /**
     * Displays to user a list of messages from the topic with pagination
     *
     * @param topicId       the id of selected Topic
     * @param page          page
     * @param pagingEnabled if output data should be divided by pages
     * @return {@code ModelAndView}
     * @throws NotFoundException when topic or branch not found
     */
    @RequestMapping(value = "/topics/{topicId}", method = RequestMethod.GET)
    public ModelAndView showTopicPage(@PathVariable(TOPIC_ID) Long topicId,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                      @RequestParam(value = PAGING_ENABLED, defaultValue = "true",
                                              required = false) Boolean pagingEnabled) throws NotFoundException {

        Topic topic = topicService.get(topicId);

        Poll poll = topic.getPoll();
        List<PollItem> pollOptions = topic.isHasPoll() ? poll.getPollItems() : null;

        Branch branch = topic.getBranch();
        JCUser currentUser = (JCUser) securityService.getCurrentUser();
        List<Post> posts = topic.getPosts();
        Pagination pag = new Pagination(page, currentUser, posts.size(), pagingEnabled);
        Integer lastReadPostIndex = lastReadPostService.getLastReadPostForTopic(topic);
        lastReadPostService.markTopicPageAsRead(topic, page, pagingEnabled);
        //todo: optimize this binding
        return new ModelAndView("postList")
                .addObject("viewList", locationService.getUsersViewing(topic))
                .addObject("usersOnline", sessionRegistry.getAllPrincipals())
                .addObject("posts", posts)
                .addObject("topic", topic)
                .addObject("pag", pag)
                .addObject("page", pag.getPage())
                .addObject("nextTopic", branch.getNextTopic(topic))
                .addObject("previousTopic", branch.getPreviousTopic(topic))
                .addObject(BRANCH_ID, branch.getId())
                .addObject(TOPIC_ID, topicId)
                .addObject("subscribed", topic.getSubscribers().contains(currentUser))
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic))
                .addObject("lastReadPost", lastReadPostIndex)
                .addObject("poll", poll)
                .addObject("pollOptions", pollOptions);
    }

    /**
     * Shows edit topic page with form, populated with fields from topic.
     *
     * @param topicId  the id of selected Topic
     * @param branchId the id of selected topic's branch
     * @return {@code ModelAndView}
     * @throws NotFoundException when topic or branch not found
     */
    @RequestMapping(value = "/topics/{topicId}/edit", method = RequestMethod.GET)
    public ModelAndView editTopicPage(@RequestParam(BRANCH_ID) Long branchId,
                                      @PathVariable(TOPIC_ID) Long topicId) throws NotFoundException {
        Topic topic = topicService.get(topicId);

        return new ModelAndView("editTopic")
                .addObject("topicDto", new TopicDto(topic))
                .addObject(BRANCH_ID, branchId)
                .addObject(TOPIC_ID, topicId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
    }

    /**
     * Saves topic after edit topic form submission.
     *
     * @param topicDto Dto populated in form
     * @param result   validation result
     * @param branchId hold the current branchId
     * @param topicId  the current topicId
     * @return {@code ModelAndView} with redirect to saved topic or the same page with validation errors, if any
     * @throws NotFoundException when topic or branch not found
     */
    @RequestMapping(value = "/topics/{topicId}/edit", method = RequestMethod.POST)
    public ModelAndView editTopic(@Valid @ModelAttribute TopicDto topicDto,
                                  BindingResult result,
                                  @RequestParam(BRANCH_ID) Long branchId,
                                  @PathVariable(TOPIC_ID) Long topicId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("editTopic")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(TOPIC_ID, topicId);
        }

        topicService.updateTopic(topicDto.getId(), topicDto.getTopicName(), topicDto.getBodyText(),
                topicDto.getTopicWeight(), topicDto.isSticked(), topicDto.isAnnouncement());

        return new ModelAndView("redirect:/topics/" + topicId);
    }

    /**
     * Moves topic to another branch.
     *
     * @param topicId  id of moving topic
     * @param branchId id of target branch
     * @throws NotFoundException when topic or branch with given id not found
     */
    @RequestMapping(value = "/topics/json/{topicId}", method = RequestMethod.POST)
    public void moveTopic(@PathVariable(TOPIC_ID) Long topicId,
                          @RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        topicService.moveTopic(topicId, branchId);
    }
}
