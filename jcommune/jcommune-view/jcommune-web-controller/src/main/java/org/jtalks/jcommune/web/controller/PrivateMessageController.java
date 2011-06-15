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

import java.util.List;
import javax.validation.Valid;
import org.jtalks.jcommune.model.entity.PrivateMessage;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.PrivateMessageService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.web.dto.PrivateMessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * MVC controller for Private Messaging. Handles request for inbox, outbox and new private messages.
 * 
 * @author Pavel Vervenko
 */
@Controller
public class PrivateMessageController {

    private final PrivateMessageService pmService;
    private final UserService userService;

    /**
     * Requires {@link PrivateMessageService} for manipulations with messages and {@link UserService} to find the 
     * recipient by username.
     * 
     * @param pmService the PrivateMessageService instance
     * @param userService the UserService instance
     */
    @Autowired
    public PrivateMessageController(PrivateMessageService pmService, UserService userService) {
        this.pmService = pmService;
        this.userService = userService;
    }

    /**
     * Render the PM inbox page with the list of incoming messages for the /inbox URI.
     * 
     * @return ModelAndView with added list of inbox messages
     */
    @RequestMapping(value = "/inbox", method = RequestMethod.GET)
    public ModelAndView displayInboxPage() {
        List<PrivateMessage> inboxForCurrentUser = pmService.getInboxForCurrentUser();
        ModelAndView modelAndView = new ModelAndView("inbox");
        modelAndView.addObject("pmList", inboxForCurrentUser);
        return modelAndView;
    }

    /**
     * Render the PM outbox page with the list of sent messages for the /outbox URI.
     * 
     * @return ModelAndView with added list of outbox messages 
     */
    @RequestMapping(value = "/outbox", method = RequestMethod.GET)
    public ModelAndView displayOutboxPage() {
        List<PrivateMessage> outboxForCurrentUser = pmService.getOutboxForCurrentUser();
        ModelAndView modelAndView = new ModelAndView("outbox");
        modelAndView.addObject("pmList", outboxForCurrentUser);
        return modelAndView;
    }

    /**
     * Render the page with a form for creation new Private Message with empty binded {@link PrivateMessageDto}.
     * 
     * @return ModelAndView with the form
     */
    @RequestMapping(value = "/new_pm", method = RequestMethod.GET)
    public ModelAndView displayNewPMPage() {
        ModelAndView mav = new ModelAndView("newPm");
        mav.addObject("privateMessageDto", new PrivateMessageDto());
        return mav;
    }

    /**
     * Save the PrivateMessage for the filled in PrivateMessageDto.
     * 
     * @param pmDto {@link PrivateMessageDto} populated in form
     * @param result result of {@link PrivateMessageDto} validation
     * @return redirect to /inbox on success or back to "/new_pm" on validation errors
     */
    @RequestMapping(value = "/new_pm", method = RequestMethod.POST)
    public ModelAndView submitNewPM(@Valid @ModelAttribute PrivateMessageDto pmDto, BindingResult result) {
        if (result.hasErrors()) {
            return new ModelAndView("newPm");
        }
        PrivateMessage newPm = PrivateMessage.createNewPrivateMessage();
        newPm.setBody(pmDto.getBody());
        newPm.setTitle(pmDto.getTitle());
        try {
            User userTo = userService.getByUsername(pmDto.getRecipient());
            newPm.setUserTo(userTo);
        } catch (UsernameNotFoundException unfe) {
            return getFormWithError();
        }
        pmService.sendMessage(newPm);
        return new ModelAndView("redirect:/outbox.html");
    }

    /**
     * Return newPm page with the flag of wrong username.
     * 
     * @return ModelAndView with error flag
     */
    private ModelAndView getFormWithError() {
        ModelAndView modelAndView = new ModelAndView("newPm");
        modelAndView.addObject("wongUser", true);
        return modelAndView;
    }
}
