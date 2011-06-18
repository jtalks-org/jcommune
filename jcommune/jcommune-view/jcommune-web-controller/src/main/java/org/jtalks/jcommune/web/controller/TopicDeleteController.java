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
 * Delete topic controller. This controller handle delete first post (delete topic) user action.
 * Before user could delete the first post he should pass through delete confirmation.
 * After user confirm delete action the topic would be removed by {@code TopicService}
 *
 * @author Teterin Alexandre
 */

@Controller
public class TopicDeleteController {
    private final TopicService topicService;

    /**
     * Constructor. Injects {@link TopicService}.
     *
     * @param topicService {@link TopicService} instance to be injected
     */
    @Autowired
    public TopicDeleteController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Redirect user to confirmation page.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing topic
     * @return {@code ModelAndView} with to parameters branchId and topicId
     */
    @RequestMapping(method = RequestMethod.GET, value = "/branch/{branchId}/topic/{topicId}/delete")
    public ModelAndView confirm(@PathVariable("topicId") Long topicId, @PathVariable("branchId") long branchId) {
        ModelAndView mav = new ModelAndView("deleteTopic");
        mav.addObject("topicId", topicId);
        mav.addObject("branchId", branchId);
        return mav;
    }

    /**
     * Handle delete action. User confirm the first post (topic) deletion.
     *
     * @param topicId  topic id, this is the topic which contains the first post which should be deleted
     * @param branchId branch containing the first topic
     * @return redirect to branch page
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/branch/{branchId}/topic/{topicId}/post/{postId}")
    public ModelAndView delete(@PathVariable("topicId") Long topicId, @PathVariable("branchId") long branchId) {
        topicService.deleteTopic(topicId);
        return new ModelAndView("redirect:/branch/" + branchId + ".html");
    }
}
