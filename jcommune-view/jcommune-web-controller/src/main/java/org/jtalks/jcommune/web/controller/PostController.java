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
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


/**
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
@Controller
public class PostController {
    public static final String TOPIC_ID = "topicId";
    public static final String BRANCH_ID = "branchId";
    public static final String POST_ID = "postId";
    private final PostService postService;
    private BreadcrumbBuilder breadcrumbBuilder;

    /**
     * Constructor. Injects {@link TopicService}.
     *
     * @param postService       {@link PostService} instance to be injected
     * @param breadcrumbBuilder the object which provides actions on
     *                          {@link org.jtalks.jcommune.web.dto.BreadcrumbBuilder} entity
     */
    @Autowired
    public PostController(PostService postService,
                          BreadcrumbBuilder breadcrumbBuilder) {
        this.postService = postService;
        this.breadcrumbBuilder = breadcrumbBuilder;
    }

    /**
     * Redirect user to confirmation page.
     *
     * @param topicId  topic id, this in topic which contains post which should be deleted
     * @param postId   post id to delete
     * @param branchId branch containing topic
     * @return {@code ModelAndView} with to parameter topicId and postId
     */
    @RequestMapping(method = RequestMethod.GET, value = "/branch/{branchId}/topic/{topicId}/post/{postId}/delete")
    public ModelAndView deleteConfirmPage(@PathVariable(TOPIC_ID) Long topicId,
                                          @PathVariable(POST_ID) Long postId,
                                          @PathVariable(BRANCH_ID) Long branchId) {
        return new ModelAndView("deletePost")
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_ID, postId)
                .addObject(BRANCH_ID, branchId);
    }

    /**
     * Delete post by given id.
     *
     * @param topicId  topic id, this in topic which contains post which should be deleted
     *                 also used for redirection back to topic.
     * @param postId   post
     * @param branchId branch containing topic
     * @return redirect to topic page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/branch/{branchId}/topic/{topicId}/post/{postId}")
    public ModelAndView delete(@PathVariable(TOPIC_ID) Long topicId,
                               @PathVariable(POST_ID) Long postId,
                               @PathVariable(BRANCH_ID) Long branchId) throws NotFoundException {
        postService.deletePost(postId);
        return new ModelAndView(new StringBuilder()
                .append("redirect:/topic/")
                .append(topicId)
                .append(".html").toString());
    }

    /**
     * Edit post by given id.
     *
     * @param topicId  topic id
     * @param postId   post id
     * @param branchId branch containing topic
     * @return redirect to post form page
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic or post not found
     */
    @RequestMapping(value = "/branch/{branchId}/topic/{topicId}/post/{postId}/edit", method = RequestMethod.GET)
    public ModelAndView edit(@PathVariable(BRANCH_ID) Long branchId,
                             @PathVariable(TOPIC_ID) Long topicId,
                             @PathVariable(POST_ID) Long postId) throws NotFoundException {
        Post post = postService.get(postId);

        return new ModelAndView("postForm")
                .addObject("postDto", PostDto.getDtoFor(post))
                .addObject(BRANCH_ID, branchId)
                .addObject(TOPIC_ID, topicId)
                .addObject(POST_ID, postId)
                .addObject("breadcrumbList", breadcrumbBuilder.getForumBreadcrumb(post.getTopic()));
    }

    /**
     * Save post.
     *
     * @param postDto  Dto populated in form
     * @param result   validation result
     * @param branchId hold the current branchId
     * @param topicId  the current topicId
     * @param postId   the current postId
     * @return {@code ModelAndView} object which will be redirect to topic page
     *         if saved successfully or show form with error message
     * @throws org.jtalks.jcommune.service.exceptions.NotFoundException
     *          when topic, branch or post not found
     */
    @RequestMapping(value = "/branch/{branchId}/topic/{topicId}/post/{postId}/save",
            method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView save(@Valid @ModelAttribute PostDto postDto,
                             BindingResult result,
                             @PathVariable(BRANCH_ID) Long branchId,
                             @PathVariable(TOPIC_ID) Long topicId,
                             @PathVariable(POST_ID) Long postId) throws NotFoundException {
        if (result.hasErrors()) {
            return new ModelAndView("postForm")
                    .addObject(BRANCH_ID, branchId)
                    .addObject(TOPIC_ID, topicId)
                    .addObject(POST_ID, postId);
        }

        postService.savePost(postDto.getId(), postDto.getBodyText());

        return new ModelAndView("redirect:/topic/" + topicId + ".html");
    }
}
