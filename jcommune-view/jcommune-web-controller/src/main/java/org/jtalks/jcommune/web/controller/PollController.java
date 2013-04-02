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

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.jcommune.web.dto.PollDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.util.Collections;

/**
 * Serves web requests for operations with poll({@link Poll}).
 *
 * @author Anuar Nurmakanov
 */
@Controller
public class PollController {
    private static final String ERROR_MESSAGE_PARAMETER = "errorMessage";
    
    private PollService pollService;

    /**
     * Constructor for controller instantiating, dependencies injected via autowiring.
     *
     * @param pollService the service which provides actions on poll
     */
    @Autowired
    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    /**
     * Adds a single vote for the option of the poll.
     * This method is needed for "single type" polls.
     *
     * @param pollId       id of poll
     * @param pollOptionId id of option of poll
     * @return data transfer object, that contains data about poll
     */
    @RequestMapping(value = "/poll/{pollId}/single", method = RequestMethod.POST)
    @ResponseBody
    public PollDto addSingleVote(@PathVariable Long pollId, @RequestParam Long pollOptionId) {
        Poll poll = pollService.vote(pollId, Collections.singletonList(pollOptionId));
        return new PollDto(poll);
    }

    /**
     * Adds a multiple votes.
     * This method needed for "multiple type" polls.
     *
     * @param pollId  id of poll
     * @param pollDto data transfer object, that contains
     *                identifiers of selected options.
     * @return data transfer object, that contains data about poll
     */
    @RequestMapping(value = "/poll/{pollId}/multiple", method = RequestMethod.POST)
    @ResponseBody
    public PollDto addMultipleVote(@PathVariable Long pollId, @RequestBody PollDto pollDto) {
        Poll poll = pollService.vote(pollId, pollDto.getPollOptionIds());
        return new PollDto(poll);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception exception) {
        MappingJacksonJsonView jsonView = new MappingJacksonJsonView();
        ModelAndView mav = new ModelAndView(jsonView);
        mav.addObject(ERROR_MESSAGE_PARAMETER, exception.getMessage());
        return mav;
    }
}
