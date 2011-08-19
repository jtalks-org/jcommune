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

import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;


/**
 * @author Kravchenko Vitaliy
 */
public class BranchControllerTest {
    private BranchService branchService;
    private TopicService topicService;
    private BranchController controller;

    @BeforeMethod
    public void init() {
        branchService = mock(BranchService.class);
        topicService = mock(TopicService.class);
        controller = new BranchController(branchService, topicService);
    }

    @Test
    public void testDisplayAllBranches() {
        when(branchService.getAll()).thenReturn(new ArrayList<Branch>());

        ModelAndView mav = controller.branchesList();

        assertViewName(mav, "branchesList");
        assertModelAttributeAvailable(mav, "topicsBranchList");
        verify(branchService).getAll();
    }

    @Test
    public void testTopicsInBranch() throws NotFoundException {
        long branchId = 1L;
        int page = 2;
        int pageSize = 5;
        int startIndex = page * pageSize - pageSize;
        when(topicService.getTopicRangeInBranch(branchId, startIndex, pageSize)).thenReturn(new ArrayList<Topic>());
        when(topicService.getTopicsInBranchCount(branchId)).thenReturn(10);

        ModelAndView mav = controller.show(branchId, page, pageSize);

        assertViewName(mav, "topicList");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);
        Long actualBranch = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        Integer actualMaxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        Integer actualPage = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((long) actualBranch, branchId);
        assertEquals((int) actualMaxPages, 2);
        assertEquals((int) actualPage, page);
        verify(topicService).getTopicRangeInBranch(branchId, startIndex, pageSize);
        verify(topicService).getTopicsInBranchCount(branchId);
    }

}
