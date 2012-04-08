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

import java.util.ArrayList;
import java.util.List;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.jcommune.web.dto.PollDto;
import org.jtalks.jcommune.web.dto.PollOptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Serves web requests for operations with poll({@link Poll}).
 * 
 * @author Anuar Nurmakanov
 *
 */
@Controller
public class PollController {
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
     * @param pollId id of poll
     * @param pollOptionId id of option of poll
     * @return data transfer object, that contains data about poll
     */
    @RequestMapping(value = "/poll/{pollId}/single", method = RequestMethod.POST)
    public @ResponseBody PollDto addSingleVote(@PathVariable Long pollId,
            @RequestParam Long pollOptionId) {
        Poll poll = pollService.addSingleVote(pollId, pollOptionId);
        return convertPollToDto(poll);
    }
    
    /**
     * Adds a multiple votes.
     * This method needed for "multiple type" polls.
     * 
     * @param pollId id of poll
     * @param pollDto data transfer object, that contains
     *                identifiers of selected options.
     * @return data transfer object, that contains data about poll
     */
    @RequestMapping(value = "/poll/{pollId}/multiple", method = RequestMethod.POST)
    public @ResponseBody PollDto addMultipleVote(@PathVariable Long pollId,
            @RequestBody PollDto pollDto) {
        List<Long> pollOptionIds = getPollOptionIds(pollDto.getPollOptions());
        Poll poll = pollService.addMultipleVote(pollId, pollOptionIds);
        return convertPollToDto(poll);
    }
    
    /**
     * Gets identifiers of selected options from data transfer objects.
     * 
     * @param pollOptionDtos list of data transfer objects, that
     *                       represents an info about selected options.
     * @return identifiers of selected options 
     */
    private List<Long> getPollOptionIds(List<PollOptionDto> pollOptionDtos) {
        List<Long> pollOptionIds = new ArrayList<Long>();
        for(PollOptionDto dto: pollOptionDtos) {
            pollOptionIds.add(dto.getId());
        }
        return pollOptionIds;
    }
    
    /**
     * Converts an instance of {@link Poll} to specific data transfer object.
     * This data transfer object needed for retrieving data to the client side.
     * 
     * @param poll an instance of {@link Poll}
     * @return data transfer object, that contains data about poll
     */
    private PollDto convertPollToDto(Poll poll) {
        List<PollOptionDto> pollOptionDtos = new ArrayList<PollOptionDto>();
        for (PollOption option : poll.getPollOptions()) {
            pollOptionDtos.add(new PollOptionDto(option.getId(), option.getVoteCount()));
        }
        return new PollDto(poll.getId(), poll.getTotalVoteCount(), pollOptionDtos);
    }
}
