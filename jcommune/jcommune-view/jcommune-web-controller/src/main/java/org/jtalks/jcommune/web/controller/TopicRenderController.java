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
import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.web.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * TopicRenderController handles GET and POST request with /topics/{topicId} Uri,
 * where topicId could be from 1 to infinity. Controller displays selected topic.
 * Class throws no exceptions
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */

@Controller
public final class TopicRenderController {

    private final PostService postService;
    private final TopicService topicService;

    /**
     * Constructor creates MVC controller with specifying PostService,
     * parameter passed via autowiring
     *
     * @param postService  {@link PostService} instance for working with {@link Post} entites
     * @param topicService {@link TopicService}  instance for working with {@link Topic} entites
     */
    @Autowired
    public TopicRenderController(PostService postService, TopicService topicService) {
        this.postService = postService;
        this.topicService = topicService;
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
    public ModelAndView showTopic(@PathVariable("branchId") long branchId,
                                  @PathVariable("topicId") long topicId,
                                  @RequestParam(value = "page", required = false) Integer page,
                                  @RequestParam(value = "size", required = false) Integer size) {

        float postsCount = postService.getPostsInTopicCount(topicId);
        Pagination pag = new Pagination(page, size, postsCount);

        List<Post> posts = postService.getPostRangeInTopic(topicId, pag.getStart(), pag.getPageSize());
        String topicTitle = topicService.get(topicId).getTitle();

        ModelAndView mav = new ModelAndView("postList");
        mav.addObject("posts", posts);
        mav.addObject("topicTitle", topicTitle);
        mav.addObject("maxPages", pag.getMaxPages());
        mav.addObject("page", pag.getPage());
        mav.addObject("branchId", branchId);
        mav.addObject("topicId", topicId);
        return mav;
    }
}
