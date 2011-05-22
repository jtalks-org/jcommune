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

import org.jtalks.jcommune.model.entity.TopicBranch;
import org.jtalks.jcommune.service.TopicBranchService;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;
import static org.mockito.Mockito.verify;


/**
 * @author Kravchenko Vitaliy
 */
public class TopicBranchControllerTest {
    private TopicBranchService topicBranchService;
    private TopicsBranchController topicsBranchController;

    @BeforeMethod
    public void init() {
        topicBranchService = mock(TopicBranchService.class);
        topicsBranchController = new TopicsBranchController(topicBranchService);
    }

    @Test
    public void testPopulateFormWithBranches(){
        List<TopicBranch> topicBranches = new ArrayList<TopicBranch>();
        TopicBranch firstTopicBranch = new TopicBranch();
        firstTopicBranch.setId(1L);
        TopicBranch secondTopicBranch = new TopicBranch();
        secondTopicBranch.setId(2L);
        topicBranches.add(firstTopicBranch);
        topicBranches.add(secondTopicBranch);
        
        when(topicBranchService.getAll()).thenReturn(topicBranches);
        assertEquals(topicBranches.size(),topicsBranchController.populateFormWithBranches().size());

        verify(topicBranchService).getAll();
    }

    @Test
    public void testDisplayAllBranches(){
        ModelAndView mav = topicsBranchController.displayAllTopicsBranches();

        assertViewName(mav, "renderAllBranches");
    }
}
