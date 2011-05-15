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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Temdegon
 */
@Controller
public class AnswerController {

    private TopicService topicService;
    private final Logger logger = LoggerFactory.getLogger(AnswerController.class);
    private final SecurityService securityService;

    @Autowired
    public AnswerController(TopicService topicService, SecurityService securityService) {
        this.topicService = topicService;
        this.securityService = securityService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/answer")
    public ModelAndView answer(@RequestParam("topicId") Long topicId) {
        logger.info("Answering to the topic " + topicId);
        if (securityService.getCurrentUser() != null) {
            ModelAndView mav = new ModelAndView("answer");
            mav.addObject("topicId", topicId);
            return mav;
        } else {
            logger.info("User doesn't logged in. Redirect to the login page");
            return new ModelAndView("redirect:/login.html");
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/answer")
    public ModelAndView answer(@RequestParam("topicId") Long topicId, @RequestParam("bodytext") String bodyText) {
        topicService.addAnswer(topicId, bodyText);
        return new ModelAndView("redirect:/topics/" + topicId + ".html");
    }
}
