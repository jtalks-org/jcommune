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

import org.jtalks.jcommune.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Osadchuck Eugeny
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
@Controller
public class PostController {
    private final TopicService topicService;

    /**
     * Constructor. Injects {@link TopicService}.
     *
     * @param topicService {@link TopicService} instance to be injected
     */
    @Autowired
    public PostController(TopicService topicService) {
        this.topicService = topicService;
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
    public ModelAndView deleteConfirmPage(@PathVariable("topicId") Long topicId,
                                          @PathVariable("postId") Long postId,
                                          @PathVariable("branchId") Long branchId) {
        return new ModelAndView("deletePost")
                .addObject("topicId", topicId)
                .addObject("postId", postId)
                .addObject("branchId", branchId);
    }

    /**
     * Delete post by given id.
     *
     * @param topicId  topic id, this in topic which contains post which should be deleted
     *                 also used for redirection back to topic.
     * @param postId   post
     * @param branchId branch containing topic
     * @return redirect to topic page
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/branch/{branchId}/topic/{topicId}/post/{postId}")
    public ModelAndView delete(@PathVariable("topicId") Long topicId,
                               @PathVariable("postId") Long postId,
                               @PathVariable("branchId") Long branchId) {
        topicService.deletePost(topicId, postId);
        return new ModelAndView(new StringBuilder()
                .append("redirect:/branch/")
                .append(branchId)
                .append("/topic/")
                .append(topicId)
                .append(".html").toString());
    }
}
