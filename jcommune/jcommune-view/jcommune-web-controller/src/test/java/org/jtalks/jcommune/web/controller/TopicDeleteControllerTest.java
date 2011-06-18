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

import java.util.HashMap;
import java.util.Map;


import org.jtalks.jcommune.service.TopicService;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeValues;

/**
 * This is test for <code>TopicDeleteController<code> class.
 * Test should cover view resolution and logic validation.
 * @author Teterin Alexandre
 *
 */
public class TopicDeleteControllerTest {
    private TopicService topicService;
    private TopicDeleteController topicDeleteController;
    
    @BeforeMethod
    public void init(){
        topicService = mock(TopicService.class);
        topicDeleteController = new TopicDeleteController(topicService);
    }
    
    
    @Test
    public void confirmTest(){     
        long topicId = 1;
        ModelAndView actualMav = topicDeleteController.confirm(topicId, 1L);
        assertViewName(actualMav, "deleteTopic");
        
        Map<String, Object> expectedModel = new HashMap<String, Object>();
        expectedModel.put("topicId", topicId);
        expectedModel.put("branchId", 1L);
        assertModelAttributeValues(actualMav, expectedModel);
        
    }
    
    @Test
    public void deleteTest(){
        long topicId = 1L;
        long branchId = 1L;
        ModelAndView actualMav = topicDeleteController.delete(topicId, branchId);
        assertViewName(actualMav, "redirect:/branch/" + branchId + ".html");
        
        verify(topicService, times(1)).deleteTopic(topicId);
    }
}
