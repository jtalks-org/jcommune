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

import org.apache.commons.lang3.ObjectUtils;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.plugin.api.web.dto.TopicDto;
import org.jtalks.jcommune.plugin.api.web.util.BreadcrumbBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

/**
 * Serves code review management web requests
 *
 * @author Vyachesla Mishcheryakov
 * @see Topic
 */
@Controller
public class CodeReviewController {

    private static final String CODE_REVIEW_VIEW = "codeReviewForm";
    public static final String BRANCH_ID = "branchId";
    public static final String BREADCRUMB_LIST = "breadcrumbList";
    private static final String SUBMIT_URL = "submitUrl";
    private static final String TOPIC_DTO = "topicDto";
    private static final String TOPIC_DRAFT = "topicDraft";
    private static final String REDIRECT_URL = "redirect:/topics/";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private BranchService branchService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private TopicModificationService topicModificationService;
    private TopicDraftService topicDraftService;
    private UserService userService;

    /**
     * @param branchService            the object which provides actions on
     *                                 {@link org.jtalks.jcommune.model.entity.Branch} entity
     * @param breadcrumbBuilder        to create Breadcrumbs for pages
     * @param topicModificationService the object which provides actions on
     * @param userService              the object which provides actions on
     *                                 {@link org.jtalks.jcommune.model.entity.JCUser} entity
     */
    @Autowired
    public CodeReviewController(BranchService branchService,
                                BreadcrumbBuilder breadcrumbBuilder,
                                TopicModificationService topicModificationService,
                                TopicDraftService topicDraftService,
                                LastReadPostService lastReadPostService,
                                UserService userService,
                                PostService postService) {
        this.branchService = branchService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.topicModificationService = topicModificationService;
        this.topicDraftService = topicDraftService;
        this.userService = userService;
    }

    /**
     * Shows page with form for new code review
     *
     * @param branchId {@link Branch} branch, where code review will be created
     * @return {@code ModelAndView} object with "codeReviewForm" view, new {@link TopicDto} and branch id
     * @throws NotFoundException when branch was not found
     */
    @RequestMapping(value = "/reviews/new", method = RequestMethod.GET)
    public ModelAndView showNewCodeReviewPage(@RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {

        TopicDraft draft = ObjectUtils.defaultIfNull(
                topicDraftService.getDraft(), new TopicDraft());

        TopicDto dto = new TopicDto(draft);

        Branch branch = branchService.get(branchId);
        dto.getTopic().setBranch(branch);

        return new ModelAndView(CODE_REVIEW_VIEW)
                .addObject(TOPIC_DTO, dto)
                .addObject(TOPIC_DRAFT, draft)
                .addObject(BRANCH_ID, branchId)
                .addObject(SUBMIT_URL, "/reviews/new?branchId=" + branchId)
                .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getNewTopicBreadcrumb(branch));
    }

    /**
     * Create code review from data entered in form
     *
     * @param topicDto object with data from form
     * @param result   {@link BindingResult} validation result
     * @param branchId branch, where topic will be created
     * @return {@code ModelAndView} object which will be redirect to forum.html
     * @throws NotFoundException when branch not found
     */
    @RequestMapping(value = "/reviews/new", method = RequestMethod.POST)
    public ModelAndView createCodeReview(@Valid @ModelAttribute TopicDto topicDto,
                                         BindingResult result,
                                         @RequestParam(BRANCH_ID) Long branchId) throws NotFoundException {
        Branch branch = branchService.get(branchId);
        if (result.hasErrors()) {
            return new ModelAndView(CODE_REVIEW_VIEW)
                    .addObject(TOPIC_DTO, topicDto)
                    .addObject(BRANCH_ID, branchId)
                    .addObject(SUBMIT_URL, "/reviews/new?branchId=" + branchId)
                    .addObject(BREADCRUMB_LIST, breadcrumbBuilder.getNewTopicBreadcrumb(branch));
        }
        Topic topic = topicDto.getTopic();
        topic.setBranch(branch);
        topic.setType(TopicTypeName.CODE_REVIEW.getName());
        Topic createdTopic = topicModificationService.createTopic(topic, topicDto.getBodyText());

        return new ModelAndView(REDIRECT_URL + createdTopic.getId());
    }

    private Topic createCodeReviewWithLockHandling(Topic topic, TopicDto topicDto) throws NotFoundException {
        for (int i = 0; i < UserController.LOGIN_TRIES_AFTER_LOCK; i++) {
            try {
                return topicModificationService.createTopic(topic, topicDto.getBodyText());
            } catch (HibernateOptimisticLockingFailureException e) {
            }
        }
        try {
            return topicModificationService.createTopic(topic, topicDto.getBodyText());
        } catch (HibernateOptimisticLockingFailureException e) {
            LOGGER.error("User has been optimistically locked and can't be reread {} times. Username: {}",
                    UserController.LOGIN_TRIES_AFTER_LOCK, userService.getCurrentUser().getUsername());
            throw e;
        }
    }

}
