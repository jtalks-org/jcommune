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
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.web.Pagination;
import org.jtalks.jcommune.web.dto.TopicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


/**
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 * @author Teterin Alexandre
 * @see Topic
 */
@Controller
public final class TopicController {

    private TopicService topicService;
    private PostService postService;

    /**
     * Constructor creates controller with objects injected via autowiring.
     *
     * @param topicService the object which provides actions on {@link Topic} entity
     * @param postService  the object which provides actions on {@link Post} entity
     */
    @Autowired
    public TopicController(TopicService topicService, PostService postService) {
        this.topicService = topicService;
        this.postService = postService;
    }

    /**
     * Method handles newTopic.html GET request and display page for creation new topic
     *
     * @param branchId {@link org.jtalks.jcommune.model.entity.Branch} id
     * @return {@code ModelAndView} object with "newTopic" view, new {@link TopicDto} and branch id
     */
    @RequestMapping(value = "/branch/{branchId}/topic/create", method = RequestMethod.GET)
    public ModelAndView createPage(@PathVariable("branchId") Long branchId) {
        return new ModelAndView("newTopic")
                .addObject("topicDto", new TopicDto())
                .addObject("branchId", branchId);
    }

    /**
     * This method handles POST requests, it will be always activated when the user pressing "Submit topic"
     *
     * @param topicDto the object that provides communication between spring form and controller
     * @param result   {@link BindingResult} object for spring validation
     * @param branchId hold the current branchId
     * @return {@code ModelAndView} object which will be redirect to forum.html
     */
    @RequestMapping(value = "/branch/{branchId}/topic", method = RequestMethod.POST)
    public ModelAndView create(@Valid @ModelAttribute TopicDto topicDto,
                               BindingResult result,
                               @PathVariable("branchId") Long branchId) {
        if (result.hasErrors()) {
            return new ModelAndView("newTopic").addObject("branchId", branchId);
        } else {
            topicService.createTopic(topicDto.getTopicName(), topicDto.getBodyText(),
                    branchId);
            //TODO: redirect to created topic
            return new ModelAndView("redirect:/branch/" + branchId + ".html");
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
    public ModelAndView deleteConfirmPage(@PathVariable("topicId") Long topicId,
                                          @PathVariable("branchId") Long branchId) {
        return new ModelAndView("deleteTopic")
                .addObject("topicId", topicId)
                .addObject("branchId", branchId);
    }

    /**
     * Handle delete action. User deleteConfirmPage the first post (topic) deletion.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing the first topic
     * @return redirect to branch page
     */
    //@RequestMapping(method = RequestMethod.DELETE, value = "/branch/{branchId}/topic/{topicId}")
    public ModelAndView delete(@PathVariable("topicId") Long topicId,
                               @PathVariable("branchId") Long branchId) {
        topicService.deleteTopic(topicId);
        return new ModelAndView("redirect:/branch/" + branchId + ".html");
    }

    /**
     * Method handles GET requests with URI /branch/{branchId}/topic/{topicId}
     * Displays to user a list of messages from the chosen theme with pagination.
     *
     * @param topicId  the id of selected Topic
     * @param page     page
     * @param size     number of posts on the page
     * @param branchId the id of selected topic branch
     * @return {@code ModelAndView}
     */
    @RequestMapping(value = "/branch/{branchId}/topic/{topicId}", method = RequestMethod.GET)
    public ModelAndView show(@PathVariable("branchId") Long branchId,
                             @PathVariable("topicId") Long topicId,
                             @RequestParam(value = "page", required = false) Integer page,
                             @RequestParam(value = "size", required = false) Integer size) {

        int postsCount = postService.getPostsInTopicCount(topicId);
        Pagination pag = new Pagination(page, size, postsCount);

        List<Post> posts = postService.getPostRangeInTopic(topicId, pag.getStart(), pag.getPageSize());
        String topicTitle = topicService.get(topicId).getTitle();

        return new ModelAndView("postList")
                .addObject("posts", posts)
                .addObject("topicTitle", topicTitle)
                .addObject("maxPages", pag.getMaxPages())
                .addObject("page", pag.getPage())
                .addObject("branchId", branchId)
                .addObject("topicId", topicId);
    }

}
