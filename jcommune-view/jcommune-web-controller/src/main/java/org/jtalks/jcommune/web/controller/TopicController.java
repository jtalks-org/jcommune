/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.controller;


import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


/**
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Teterin Alexandre
 * @author Max Malakhov
 * @see Topic
 */
@Controller
public final class TopicController {

    public static final String TOPIC_ID = "topicId";
    public static final String BRANCH_ID = "branchId";
    public static final String BREADCRUMB_LIST = "breadcrumbList";

    private TopicService topicService;
    private PostService postService;
    private BranchService branchService;
    private BreadcrumbBuilder breadcrumbBuilder;

    /**
     * Constructor creates controller with objects injected via autowiring.
     *
     * @param topicService      the object which provides actions on {@link Topic} entity
     * @param postService       the object which provides actions on {@link Post} entity
     * @param branchService     the object which provides actions on {@link org.jtalks.jcommune.model.entity.Branch} entity
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     */
    @Autowired
    public TopicController(TopicService topicService, PostService postService,
                           BranchService branchService,
                           BreadcrumbBuilder breadcrumbBuilder) {
        this.topicService = topicService;
        this.postService = postService;
        this.branchService = branchService;
        this.breadcrumbBuilder = breadcrumbBuilder;
    }

    /**
     * Method handles newTopic.html GET request and display page for creation new topic
     *
     * @param branchId {@link org.jtalks.jcommune.model.entity.Branch} id
     * @return {@code ModelAndView} object with "newTopic" view, new {@link TopicDto} and branch id
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/branch/{branchId}/topic/create", method = RequestMethod.GET)
    public ModelAndView createPage(@PathVariable(BRANCH_ID) Long branchId) throws NotFoundException {
        return new ModelAndView("newTopic")
                .addObject("topicDto", new TopicDto())
                .addObject("branchId", branchId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)));
    }

    /**
     * This method handles POST requests, it will be always activated when the user pressing "Submit topic"
     *
     * @param topicDto the object that provides communication between spring form and controller
     * @param result   {@link BindingResult} object for spring validation
     * @param branchId hold the current branchId
     * @return {@code ModelAndView} object which will be redirect to forum.html
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/branch/{branchId}/topic", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView create(@Valid @ModelAttribute TopicDto topicDto,
                               BindingResult result,
                               @PathVariable(BRANCH_ID) Long branchId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("newTopic")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)));
        } else {
            Topic createdTopic = topicService.createTopic(topicDto.getTopicName(), topicDto.getBodyText(),
                    branchId);
            return new ModelAndView("redirect:/topic/"
                    + createdTopic.getId() + ".html");
        }
    }

    /**
     * Redirect user to confirmation page.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing topic
     * @return {@code ModelAndView} with to parameters branchId and topicId
     */
    @RequestMapping(method = RequestMethod.GET, value = "/branch/{branchId}/topic/{topicId}/delete")
    public ModelAndView deleteConfirmPage(@PathVariable(TOPIC_ID) Long topicId,
                                          @PathVariable(BRANCH_ID) Long branchId) {
        return new ModelAndView("deleteTopic")
                .addObject(TOPIC_ID, topicId)
                .addObject(BRANCH_ID, branchId);
    }

    /**
     * Handle delete action. User deleteConfirmPage the first post (topic) deletion.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing the first topic
     * @return redirect to branch page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/branch/{branchId}/topic/{topicId}")
    public ModelAndView delete(@PathVariable(TOPIC_ID) Long topicId,
                               @PathVariable(BRANCH_ID) Long branchId) throws NotFoundException {
        topicService.deleteTopic(topicId);
        return new ModelAndView("redirect:/branch/" + branchId + ".html");
    }

    /**
     * Method handles GET requests with URI /topic/{topicId}
     * Displays to user a list of messages from the chosen theme with pagination.
     *
     * @param topicId the id of selected Topic
     * @param page    page
     * @param size    number of posts on the page
     * @return {@code ModelAndView}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(value = "/topic/{topicId}", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView show(@PathVariable(TOPIC_ID) Long topicId,
                             @RequestParam(value = "page", required = false) Integer page,
                             @RequestParam(value = "size", required = false) Integer size) throws NotFoundException {

        Topic topic = topicService.get(topicId);

        Long branchId = topic.getBranch().getId();

        int postsCount = postService.getPostsInTopicCount(topicId);
        Pagination pag = new Pagination(page, size, postsCount);

        List<Post> posts = postService.getPostRangeInTopic(topicId, pag.getStart(), pag.getPageSize());

        return new ModelAndView("postList")
                .addObject("posts", posts)
                .addObject("topic", topic)
                .addObject("maxPages", pag.getMaxPages())
                .addObject("page", pag.getPage())
                .addObject(BRANCH_ID, branchId)
                .addObject(TOPIC_ID, topicId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic))
                ;
    }

    /**
     * Method handles GET requests with URI /branch/{branchId}/topic/{topicId}
     * Displays to user a list of messages from the chosen theme with pagination.
     *
     * @param topicId  the id of selected Topic
     * @param branchId the id of selected topic branch
     * @return {@code ModelAndView}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(value = "/branch/{branchId}/topic/{topicId}/edit", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable(BRANCH_ID) Long branchId,
                             @PathVariable(TOPIC_ID) Long topicId) throws NotFoundException {
        Topic topic = topicService.get(topicId);

        return new ModelAndView("topicForm")
                .addObject("topicDto", TopicDto.getDtoFor(topic))
                .addObject(BRANCH_ID, branchId)
                .addObject(TOPIC_ID, topicId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
    }

    /**
     * Save topic.
     *
     * @param topicDto Dto populated in form
     * @param result   validation result
     * @param branchId hold the current branchId
     * @param topicId  the current topicId
     * @return {@code ModelAndView} object which will be redirect to forum.html
     *         if saved successfully or show form with error message
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(value = "/branch/{branchId}/topic/{topicId}/save", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView save(@Valid @ModelAttribute TopicDto topicDto,
                             BindingResult result,
                             @PathVariable(BRANCH_ID) Long branchId,
                             @PathVariable(TOPIC_ID) Long topicId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("topicForm")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(TOPIC_ID, topicId);
        }

        topicService.saveTopic(topicDto.getId(), topicDto.getTopicName(), topicDto.getBodyText(),
                topicDto.getTopicWeight(), topicDto.isSticked(), topicDto.isAnnouncement());

        return new ModelAndView("redirect:/topic/" + topicId + ".html");
    }
}
