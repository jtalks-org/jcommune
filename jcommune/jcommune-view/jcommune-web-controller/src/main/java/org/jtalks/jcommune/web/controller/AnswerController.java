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

import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.UserNotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for Topic answer related actions.
 * 
 * @author Pavel Vervenko
 */
@Controller
public class AnswerController {

    private TopicService topicService;
    private final Logger logger = LoggerFactory.getLogger(AnswerController.class);
    private final SecurityService securityService;

    /**
     * Constructor creates MVC controller with specifying TopicService, SecurityService.
     * 
     * @param topicService {@link TopicService} to be injected
     * @param securityService {@link SecurityService} to be injected
     */
    @Autowired
    public AnswerController(TopicService topicService, SecurityService securityService) {
        this.topicService = topicService;
        this.securityService = securityService;
    }
    
    /**
     * Creates the answering page with empty answer form.
     * If the user isn't logged in he will be redirected to the login page.
     * @param topicId the id of the topic for the answer
     * @return answering <code>ModelAndView</code> or redirect to the login page
     */
    @RequestMapping(method = RequestMethod.GET, value = "/answer")
    public ModelAndView answer(@RequestParam("topicId") Long topicId) {
        logger.info("Answering to the topic " + topicId);
        if (securityService.getCurrentUser() != null) {
            ModelAndView mav = new ModelAndView("answer");
            mav.addObject("topicId", topicId);
            return mav;
        } else {
            logger.info("User doesn't logged in. Redirect to the login page");
            return getLoginPageRedirect();
        }
    }

    /**
     * Process the answer form. Adds new post to the specified topic and redirects to the topic view page.
     * @param topicId the id of the answered topic
     * @param bodyText the content of the answer
     * @return redirect to the topic view
     */
    @RequestMapping(method = RequestMethod.POST, value = "/answer")
    public ModelAndView answer(@RequestParam("topicId") Long topicId, @RequestParam("bodytext") String bodyText) {
        try {
            topicService.addAnswer(topicId, bodyText);
        } catch (UserNotLoggedInException e) {
            logger.warn("The answer is posted but current user wan't found. Redirecting to login page.");
            return getLoginPageRedirect();
        }
        return new ModelAndView("redirect:/topics/" + topicId + ".html");
    }
    
    /**
     * Creates the redirect to the login page.
     * @return redirect to the login page
     */
    private ModelAndView getLoginPageRedirect() {
        return new ModelAndView("redirect:/login.html");
    }
}
