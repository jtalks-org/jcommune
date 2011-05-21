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

import org.jtalks.jcommune.service.PostService;
import org.jtalks.jcommune.service.TopicService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Delete post controller.
 * @author Osadchuck Eugeny
 *
 */
@Controller
public class DeletePostController {
    private final Logger logger = LoggerFactory.getLogger(DeletePostController.class);
    
    PostService postSercice;

    @Autowired
    public DeletePostController(PostService postSercice) {
        this.postSercice = postSercice;
    }
    
    @RequestMapping(method = RequestMethod.GET, value = "/deletePost")
    public ModelAndView confirm(@RequestParam("topicId") Long topicId, @RequestParam("postId") Long postId){
        ModelAndView mav = new ModelAndView("deletePost");
        mav.addObject("topicId", topicId);
        mav.addObject("postId", postId);
        return mav;
    }
    
    @RequestMapping(method = RequestMethod.DELETE, value = "/deletePost")
    public ModelAndView delete(@RequestParam("topicId") Long topicId, @RequestParam("postId") Long postId){
        logger.debug("User confirm post removing postId = " + postId);
        postSercice.delete(postId);
        return new ModelAndView("redirect:/topics/" + topicId + ".html");
    }
}
