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

import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.PostDto;
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


/**
 * Controller for post-related actions
 *
 * @author Pavel Vervenko
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Alexandre Teterin
 */
@Controller
public class PostController {

    public static final String TOPIC_ID = "topicId";
    public static final String POST_ID = "postId";
    public static final int MIN_ANSWER_LENGTH = 1;

    private PostService postService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private TopicService topicService;

    /**
     * Constructor. Injects {@link PostService}.
     *
     * @param postService       {@link org.jtalks.jcommune.service.PostService} instance to be injected
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     * @param topicService {@link TopicService} to be injected
     */
    @Autowired
    public PostController(PostService postService,
                          BreadcrumbBuilder breadcrumbBuilder, TopicService topicService) {
        this.postService = postService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.topicService = topicService;
    }

    /**
     * Redirect user to confirmation page.
     *
     * @param topicId topic id, this in topic which contains post which should be deleted
     * @param postId  post id to delete
     * @return {@code ModelAndView} with to parameter topicId and postId
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/{postId}/delete")
    public ModelAndView deleteConfirmPage(@RequestParam(TOPIC_ID) Long topicId,
                                          @PathVariable(POST_ID) Long postId) {
        return new ModelAndView("deletePost")
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_ID, postId);
    }

    /**
     * Delete post by given id.
     *
     * @param topicId topic id, this in topic which contains post which should be deleted
     *                also used for redirection back to topic.
     * @param postId  post
     * @return redirect to topic page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/posts/{postId}")
    public ModelAndView delete(@RequestParam(TOPIC_ID) Long topicId,
                               @PathVariable(POST_ID) Long postId) throws NotFoundException {
        postService.deletePost(postId);
        return new ModelAndView(new StringBuilder()
                .append("redirect:/topics/")
                .append(topicId).toString());
    }

    /**
     * Edit post by given id.
     *
     * @param topicId topic id
     * @param postId  post id
     * @return redirect to post form page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.GET)
    public ModelAndView edit(@RequestParam(TOPIC_ID) Long topicId,
                             @PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);

        return new ModelAndView("postForm")
                .addObject("postDto", PostDto.getDtoFor(post))
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_ID, postId)
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(post.getTopic()));
    }

    /**
     * Save post.
     *
     * @param postDto Dto populated in form
     * @param result  validation result
     * @param topicId the current topicId
     * @param postId  the current postId
     * @return {@code ModelAndView} object which will be redirect to topic page
     *         if saved successfully or show form with error message
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic, branch or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.POST)
    public ModelAndView save(@Valid @ModelAttribute PostDto postDto,
                             BindingResult result,
                             @RequestParam(TOPIC_ID) Long topicId,
                             @PathVariable(POST_ID) Long postId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("postForm")
                    .addObject(TOPIC_ID, topicId)
                    .addObject(POST_ID, postId);
        }

        postService.savePost(postDto.getId(), postDto.getBodyText());

        return new ModelAndView("redirect:/topics/" + topicId);
    }

      /**
     * Creates the answering page with empty answer form.
     * If the user isn't logged in he will be redirected to the login page.
     *
     * @param topicId         the id of the topic for the answer
     * @param validationError is true when post length is less than 2
     * @return answering {@code ModelAndView} or redirect to the login page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/new")
    public ModelAndView createPage(@RequestParam("topicId") Long topicId,
                                   @RequestParam(value = "validationError", required = false)
                                   Boolean validationError) throws NotFoundException {
        ModelAndView mav = new ModelAndView("answer");
        Topic answeringTopic = topicService.get(topicId);
        mav.addObject("topic", answeringTopic);
        mav.addObject("topicId", topicId);
        mav.addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(answeringTopic));
        if (validationError != null && validationError) {
            mav.addObject("validationError", validationError);
        }
        return mav;
    }

    /**
     * Process the answer form. Adds new post to the specified topic and redirects to the
     * topic view page.
     *
     * @param topicId  the id of the answered topic
     * @param bodyText the content of the answer
     * @return redirect to the topic view
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(method = RequestMethod.POST, value = "/posts/new")
    public ModelAndView create(@RequestParam("topicId") Long topicId,
                               @RequestParam("bodytext") String bodyText) throws NotFoundException {
        if (isValidAnswer(bodyText)) {
            topicService.addAnswer(topicId, bodyText);
            return new ModelAndView("redirect:/topics/" + topicId);
        } else {
            return createPage(topicId, true);
        }

    }

    /**
     * Check the answer length.
     *
     * @param bodyText answer content
     * @return true if answer is valid
     */
    private boolean isValidAnswer(String bodyText) {
        return bodyText.trim().length() > MIN_ANSWER_LENGTH;
    }
}
