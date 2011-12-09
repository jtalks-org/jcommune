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
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.dto.PostDto;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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
 * @author Evgeniy Naumenko
 */
@Controller
public class PostController {

    public static final String TOPIC_ID = "topicId";
    public static final String POST_ID = "postId";

    private PostService postService;
    private BreadcrumbBuilder breadcrumbBuilder;
    private TopicService topicService;
    private SecurityService securityService;

    /**
     * Constructor. Injects {@link PostService}.
     *
     * @param postService       {@link org.jtalks.jcommune.service.PostService} instance to be injected
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     * @param topicService      {@link TopicService} to be injected
     * @param securityService   {@link SecurityService} to retrieve current logged in user
     */
    @Autowired
    public PostController(PostService postService, BreadcrumbBuilder breadcrumbBuilder,
                          TopicService topicService, SecurityService securityService) {
        this.postService = postService;
        this.breadcrumbBuilder = breadcrumbBuilder;
        this.topicService = topicService;
        this.securityService = securityService;
    }

    /**
     * Show confirmation page.
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
     * Edit post page filled with data from post with given id.
     *
     * @param topicId topic id
     * @param postId  post id
     * @return redirect to post form page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    @RequestMapping(value = "/posts/{postId}/edit", method = RequestMethod.GET)
    public ModelAndView editPage(@RequestParam(TOPIC_ID) Long topicId,
                                 @PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);

        return new ModelAndView("postForm")
                .addObject("postDto", PostDto.getDtoFor(post))
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_ID, postId)
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(post.getTopic()));
    }

    /**
     * Update existing post.
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
    public ModelAndView update(@Valid @ModelAttribute PostDto postDto,
                               BindingResult result,
                               @RequestParam(TOPIC_ID) Long topicId,
                               @PathVariable(POST_ID) Long postId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("postForm")
                    .addObject(TOPIC_ID, topicId)
                    .addObject(POST_ID, postId);
        }

        postService.updatePost(postDto.getId(), postDto.getBodyText());

        return new ModelAndView("redirect:/topics/" + topicId);
    }

    /**
     * Creates the answering page with empty answer form.
     *
     * @param topicId the id of the topic for the answer
     * @return answering {@code ModelAndView} or redirect to the login page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic not found
     */
    @RequestMapping(method = RequestMethod.GET, value = "/posts/new")
    public ModelAndView addPost(@RequestParam(TOPIC_ID) Long topicId) throws NotFoundException {
        Topic answeringTopic = topicService.get(topicId);
        return new ModelAndView("answer")
                .addObject("topic", answeringTopic)
                .addObject(TOPIC_ID, topicId)
                .addObject("postDto", new PostDto())
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(answeringTopic));
    }


    /**
     * Creates the answering page with quotation.
     * If user select nothing JS will substitute whole post contents here
     * <p/>
     * Supports post method to pass large quotations.
     * Supports get method as language switching always use get requests.
     *
     * @param topicId   topic id to answer to
     * @param selection text selected by user for the quotation.
     * @return the same view as topic answerring page with textarea prefilled with quted text
     * @throws NotFoundException when topic was not found
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, value = "/posts/quote")
    public ModelAndView addPostWithQuote(@RequestParam(TOPIC_ID) Long topicId,
                                         @RequestParam("selection") String selection) throws NotFoundException {
        // todo: move these constants to BB converter when ready
        String quote = "[quote]" + selection + "[/quote]";
        ModelAndView mav = addPost(topicId);
        PostDto dto = (PostDto) mav.getModel().get("postDto");
        dto.setBodyText(quote);
        return mav;
    }

    /**
     * Process the reply form. Adds new post to the specified topic and redirects to the
     * topic view page.
     *
     * @param postDto dto that contains data entered in form
     * @param result  validation result
     * @return redirect to the topic or back to answer pae if validation failed
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or branch not found
     */
    @RequestMapping(method = RequestMethod.POST, value = "/posts/new")
    public ModelAndView create(@Valid @ModelAttribute PostDto postDto, BindingResult result) throws NotFoundException {
        if (result.hasErrors()) {
            ModelAndView mav = new ModelAndView("answer");
            mav.addObject(postDto);
            return mav;
        }
        Post newbie = topicService.replyToTopic(postDto.getTopicId(), postDto.getBodyText());
        int pagesize = Pagination.getPageSizeFor(securityService.getCurrentUser());
        int lastPage = newbie.getTopic().getLastPageNumber(pagesize);
        return new ModelAndView(new StringBuilder("redirect:/topics/")
                .append(postDto.getTopicId())
                .append("?page=")
                .append(lastPage)
                .append("#")
                .append(newbie.getId())
                .toString());
    }


}
