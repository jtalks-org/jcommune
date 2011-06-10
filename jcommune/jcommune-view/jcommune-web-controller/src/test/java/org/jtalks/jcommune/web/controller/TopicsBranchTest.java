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

import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.TopicService;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;


public class TopicsBranchTest {
    private TopicService topicService;
    private TopicsAccordingToBranchController controller;

    @BeforeMethod
    public void init() {
        topicService = mock(TopicService.class);
        controller = new TopicsAccordingToBranchController(topicService);
    }

    @Test
    public void testTopicsInBranch() {
        long branchId = 1L;
        int page = 2;
        int size = 5;
        int start = page*size-size;
        when(topicService.getTopicRangeInBranch(branchId, start, size)).thenReturn(new ArrayList<Topic>());
        when(topicService.getTopicsInBranchCount(branchId)).thenReturn(10);

        ModelAndView mav = controller.topicsInBranch(branchId, page, size);

        assertViewName(mav, "topicList");
        assertAndReturnModelAttributeOfType(mav,"topics", List.class);
        Long _branch = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals((long)_branch, branchId);
        Integer _maxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        Integer _page = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((int)_maxPages, 2);
        assertEquals((int)_page, page);
        verify(topicService, times(1)).getTopicRangeInBranch(branchId, start, size);
        verify(topicService, times(1)).getTopicsInBranchCount(branchId);
    }

}
