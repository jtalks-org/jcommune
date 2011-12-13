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


import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
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
    private static final String PAGING_ENABLED = "pagingEnabled";

    private TopicService topicService;
    private BranchService branchService;
    private SecurityService securityService;
    private BreadcrumbBuilder breadcrumbBuilder;

    /**
     * This method turns the trim binder on. Trim bilder
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
     * Constructor creates controller with objects injected via autowiring.
     *
     * @param topicService      the object which provides actions on {@link Topic} entity
     * @param branchService     the object which provides actions on
     * @param securityService   autowired object from Spring Context
     *                          {@link org.jtalks.jcommune.model.entity.Branch} entity
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     */
    @Autowired
    public TopicController(TopicService topicService,
                           BranchService branchService,
                           SecurityService securityService,
                           BreadcrumbBuilder breadcrumbBuilder) {
        this.topicService = topicService;
        this.branchService = branchService;
        this.securityService = securityService;
        this.breadcrumbBuilder = breadcrumbBuilder;
    }

    /**
     * Shows page with form for new topic.
     *
     * @param branchId {@link org.jtalks.jcommune.model.entity.Branch} branch, where topic will be created
     * @return {@code ModelAndView} object with "newTopic" view, new {@link TopicDto} and branch id
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/topics/new", method = RequestMethod.GET)
    public ModelAndView createPage(@RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        return new ModelAndView("newTopic")
                .addObject("topicDto", new TopicDto())
                .addObject("branchId", branchId)
                .addObject(BREADCRUMB_LIST,
                        breadcrumbBuilder.getNewTopicBreadcrumb(branchService.get(branchId)));
    }

    /**
     * Create topic from data entered in form.
     *
     * @param topicDto object with data from form
     * @param result   {@link BindingResult} validation result
     * @param branchId branch, where topic will be created
     * @return {@code ModelAndView} object which will be redirect to forum.html
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when branch not found
     */
    @RequestMapping(value = "/topics/new", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute TopicDto topicDto,
                               BindingResult result,
                               @RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("newTopic")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)));
        }

        Topic createdTopic = topicService.createTopic(topicDto.getTopicName(), topicDto.getBodyText(), branchId);
        return new ModelAndView("redirect:/topics/" + createdTopic.getId());
    }

    /**
     * Page with confirmation.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing topic
     * @return {@code ModelAndView} with to parameters branchId and topicId
     */
    @RequestMapping(method = RequestMethod.GET, value = "/topics/{topicId}/delete")
    public ModelAndView deleteConfirmPage(@PathVariable(TOPIC_ID) Long topicId,
                                          @RequestParam(BRANCH_ID) Long branchId) {
        return new ModelAndView("deleteTopic")
                .addObject(TOPIC_ID, topicId)
                .addObject(BRANCH_ID, branchId);
    }

    /**
     * Delete topic.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing the first topic
     * @return redirect to branch page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    @RequestMapping(value = "/topics/{topicId}", method = RequestMethod.DELETE)
    public ModelAndView delete(@PathVariable(TOPIC_ID) Long topicId,
                               @RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        topicService.deleteTopic(topicId);
        return new ModelAndView("redirect:/branches/" + branchId);
    }

    /**
     * Displays to user a list of messages from the topic with pagination.
     *
     * @param topicId       the id of selected Topic
     * @param page          page
     * @param pagingEnabled number of posts on the page
     * @return {@code ModelAndView}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(value = "/topics/{topicId}", method = RequestMethod.GET)
    public ModelAndView showTopicPage(@PathVariable(TOPIC_ID) Long topicId,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                      @RequestParam(value = PAGING_ENABLED, defaultValue = "true",
                                              required = false) Boolean pagingEnabled) throws NotFoundException {

        Topic topic = topicService.get(topicId);
        Branch branch = topic.getBranch();

        User currentUser = securityService.getCurrentUser();

        List<Post> posts = topic.getPosts();
        Pagination pag = new Pagination(page, currentUser, posts.size(), pagingEnabled);

        return new ModelAndView("postList")
                .addObject("posts", posts)
                .addObject("topic", topic)
                .addObject("pag", pag)
                .addObject("page", pag.getPage())
                .addObject("nextTopic", branch.getNextTopic(topic))
                .addObject("previousTopic", branch.getPreviousTopic(topic))
                .addObject(BRANCH_ID, branch.getId())
                .addObject(TOPIC_ID, topicId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getForumBreadcrumb(topic));
    }

    /**
     * Edit page with form, populated with fields from topic.
     *
     * @param topicId  the id of selected Topic
     * @param branchId the id of selected topic branch
     * @return {@code ModelAndView}
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(value = "/topics/{topicId}/edit", method = RequestMethod.GET)
    public ModelAndView editTopicPage(@RequestParam(BRANCH_ID) Long branchId,
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
    @RequestMapping(value = "/topics/{topicId}/edit", method = RequestMethod.POST)
    public ModelAndView editTopic(@Valid @ModelAttribute TopicDto topicDto,
                                  BindingResult result,
                                  @RequestParam(BRANCH_ID) Long branchId,
                                  @PathVariable(TOPIC_ID) Long topicId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("topicForm")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(TOPIC_ID, topicId);
        }

        topicService.updateTopic(topicDto.getId(), topicDto.getTopicName(), topicDto.getBodyText(),
                topicDto.getTopicWeight(), topicDto.isSticked(), topicDto.isAnnouncement());

        return new ModelAndView("redirect:/topics/" + topicId);
    }
}
