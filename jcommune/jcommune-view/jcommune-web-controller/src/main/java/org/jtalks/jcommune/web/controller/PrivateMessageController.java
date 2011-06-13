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
import org.jtalks.jcommune.web.dto.TopicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Pavel Vervenko
 */
@Controller
public class PrivateMessageController {
    
    private final PrivateMessageService pmService;
    private final UserService userService;

    @Autowired
    public PrivateMessageController(PrivateMessageService pmService, UserService userService) {
        this.pmService = pmService;
        this.userService = userService;
    }
    
    @RequestMapping(value = "/inbox", method = RequestMethod.GET)
    public ModelAndView displayInboxPage() {
        List<PrivateMessage> inboxForCurrentUser = pmService.getInboxForCurrentUser();
        ModelAndView modelAndView = new ModelAndView("inbox");
        modelAndView.addObject("pmList", inboxForCurrentUser);
        return modelAndView;
    }

    @RequestMapping(value = "/outbox", method = RequestMethod.GET)
    public ModelAndView displayOutboxPage() {
        List<PrivateMessage> outboxForCurrentUser = pmService.getOutboxForCurrentUser();
        ModelAndView modelAndView = new ModelAndView("outbox");
        modelAndView.addObject("pmList", outboxForCurrentUser);
        return modelAndView;
    }

    @RequestMapping(value = "/new_pm", method = RequestMethod.GET)
    public ModelAndView displayNewPMPage() {
        ModelAndView mav = new ModelAndView("newPm");
        mav.addObject("privateMessageDto", new PrivateMessageDto());
        return mav;
    }

    @RequestMapping(value = "/new_pm", method = RequestMethod.POST)
    public ModelAndView submitNewPM(@Valid @ModelAttribute PrivateMessageDto pmDto,  BindingResult result) {
        
        if (result.hasErrors()) {
            return new ModelAndView("newPm");
        }

        PrivateMessage newPm = PrivateMessage.createNewPrivateMessage();
        newPm.setBody(pmDto.getBody());
        newPm.setTitle(pmDto.getTitle());
        User userTo = userService.getByUsername(pmDto.getRecipient());
        newPm.setUserTo(userTo);
        pmService.sendMessage(newPm);
        
        return new ModelAndView("redirect:/outbox.html");
    }
}
