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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jtalks.jcommune.model.entity.Poll;
import org.jtalks.jcommune.model.entity.PollItem;
import org.jtalks.jcommune.service.PollService;
import org.jtalks.jcommune.web.dto.PollDto;
import org.jtalks.jcommune.web.dto.PollOptionDto;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Anuar Nurmakanov
 */
public class PollControllerTest {
    private static final Long POLL_ID = 1L;
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
        long pollOptionId = 1;
        Poll poll = createPoll(POLL_ID, Arrays.asList(pollOptionId));

        Mockito.when(pollService.vote(POLL_ID, Arrays.asList(pollOptionId))).thenReturn(poll);

        PollDto pollDto = pollController.addSingleVote(POLL_ID, pollOptionId);
        PollOptionDto optionDto = pollDto.getPollOptions().get(0);

        Assert.assertEquals(pollDto.getId(), poll.getId(), "The id must be the same.");
        Assert.assertEquals(optionDto.getId(), pollOptionId, "The id must be the same");
    }

    @Test
    public void testAddMultipleVote() {
        long firstOptionId = 1;
        long secondOptionId = 2;
        List<Long> optionIds = Arrays.asList(firstOptionId, secondOptionId);
        Poll poll = createPoll(POLL_ID, optionIds);
        PollDto pollDto = new PollDto(poll);

        Mockito.when(pollService.vote(POLL_ID, optionIds)).thenReturn(poll);

        PollDto resultPollDto = pollController.addMultipleVote(POLL_ID, pollDto);

        Assert.assertEquals(resultPollDto.getId(), poll.getId(), "The id must be the same.");
        PollOptionDto firstOptionDto = resultPollDto.getPollOptions().get(0);
        Assert.assertEquals(firstOptionDto.getId(), firstOptionId,
                "The id must be the same");
        PollOptionDto secondOptionDto = resultPollDto.getPollOptions().get(1);
        Assert.assertEquals(secondOptionDto.getId(), secondOptionId,
                "The id must be the same");

    }
    
    @Test
    public void testHandleAllExceptions() {
        Exception exception = new Exception("Some message");
        ModelAndView mav = pollController.handleAllExceptions(exception);

        assertTrue(mav.getView() instanceof MappingJacksonJsonView);
        assertEquals(mav.getModel().get("errorMessage"), exception.getMessage());
    }

    private Poll createPoll(long pollId, List<Long> pollOptionIds) {
        Poll poll = new Poll("New poll");
        poll.setId(pollId);
        int voteCount = 10;
        for (Long optionId : pollOptionIds) {
            PollItem option = createPollOption("First option", optionId, voteCount);
            poll.addPollOptions(option);
        }
        return poll;
    }

    private PollItem createPollOption(String name, Long id, int voteCount) {
        PollItem option = new PollItem("New poll option");
        option.setVotesCount(voteCount);
        option.setId(id);
        return option;
    }
    
}
