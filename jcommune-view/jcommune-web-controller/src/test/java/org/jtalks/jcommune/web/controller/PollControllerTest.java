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

import java.util.Arrays;
import java.util.List;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollOption;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.jcommune.web.dto.PollDto;
import org.jtalks.jcommune.web.dto.PollOptionDto;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar Nurmakanov
 *
 */
public class PollControllerTest {
    @Mock
    private PollService pollService;
    private PollController pollController;
    
    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        pollController = new PollController(pollService);
    }
    
    @Test
    public void testAddSingleVote() {
        long pollId = 1;
        Poll poll = new Poll("New poll");
        poll.setId(pollId);
        long pollOptionId = 1;
        int voteCount = 10;
        PollOption option = new PollOption("New poll option");
        option.setVoteCount(voteCount);
        option.setId(pollOptionId);
        poll.addPollOption(option);
        
        Mockito.when(pollService.addSingleVote(pollId, pollOptionId)).thenReturn(poll);
        
        PollDto pollDto = pollController.addSingleVote(pollId, pollOptionId);
        PollOptionDto optionDto = pollDto.getPollOptions().get(0);
        
        Assert.assertEquals(pollDto.getId(), poll.getId(), "The id must be the same.");
        Assert.assertEquals(optionDto.getId(), option.getId(), "The id must be the same");
    }
    
    @Test
    public void testAddMultipleVote() {
        long pollId = 1;
        Poll poll = new Poll("New poll");
        poll.setId(pollId);
        long firstOptionId = 1;
        long secondOptionId = 2;
        List<Long> optionIds = Arrays.asList(firstOptionId, secondOptionId);
        int voteCount = 10;
        PollOption firstOption = createPollOption("First option", firstOptionId, voteCount);
        PollOption secondOption = createPollOption("Second option", secondOptionId, voteCount);
        poll.addPollOption(firstOption);
        poll.addPollOption(secondOption);
        PollDto pollDto = PollDto.getDtoFor(poll);
        
        
        Mockito.when(pollService.addMultipleVote(pollId, optionIds)).thenReturn(poll);
        
        PollDto resultPollDto = pollController.addMultipleVote(pollId, pollDto);
        Assert.assertEquals(resultPollDto.getId(), poll.getId(), "The id must be the same.");
        PollOptionDto firstOptionDto = resultPollDto.getPollOptions().get(0);
        Assert.assertEquals(firstOptionDto.getId(), firstOptionId,
                "The id must be the same");
        PollOptionDto secondOptionDto = resultPollDto.getPollOptions().get(1);
        Assert.assertEquals(secondOptionDto.getId(), secondOptionId,
                "The id must be the same");
        
    }
    
    private PollOption createPollOption(String name, Long id, int voteCount) {
        PollOption option = new PollOption("New poll option");
        option.setVoteCount(voteCount);
        option.setId(id);
        return option;
    }
}
