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

import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.assertAndReturnModelAttributeOfType;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;
import static org.springframework.test.web.ModelAndViewAssert.assertViewName;
import static org.testng.Assert.assertEquals;


/**
 * @author Kravchenko Vitaliy
 * @author Alexandre Teterin
 */
public class BranchControllerTest {
    private BranchService branchService;
    private TopicService topicService;
    private SecurityService securityService;
    private BranchController controller;
    private BreadcrumbBuilder breadcrumbBuilder;

    private static final DateTime now = new DateTime();

    @BeforeMethod
    public void init() {
        branchService = mock(BranchService.class);
        topicService = mock(TopicService.class);
        securityService = mock(SecurityService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller = new BranchController(branchService, topicService, securityService, breadcrumbBuilder);
    }

    @Test
    public void showPage() throws NotFoundException {
        long branchId = 1L;
        int page = 2;
        boolean pagingEnabled = true;
        //set expectations
        when(branchService.get(branchId)).thenReturn(new Branch("name"));
        when(breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)))
                .thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.showPage(branchId, page, pagingEnabled);

        //check expectations
        verify(breadcrumbBuilder).getForumBreadcrumb(branchService.get(branchId));

        //check result
        assertViewName(mav, "topicList");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Long actualBranch = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals((long) actualBranch, branchId);

        Integer actualPage = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((int) actualPage, page);

        assertModelAttributeAvailable(mav, "breadcrumbList");

    }

    @Test
    public void recentTopicsPage() throws NotFoundException {
        int page = 2;
        int pageSize = 50;
        int startIndex = page * pageSize - pageSize;
        //set expectations
        when(topicService.getTopicsPastLastDayCount(now)).thenReturn(10);
        when(topicService.getAllTopicsPastLastDay(startIndex, pageSize, now)).thenReturn(new ArrayList<Topic>());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("lastlogin", now);

        //invoke the object under test
        ModelAndView mav = controller.recentTopicsPage(page, session);

        //check expectations
        verify(topicService).getAllTopicsPastLastDay(startIndex, pageSize, now);
        verify(topicService).getTopicsPastLastDayCount(now);

        //check result
        assertViewName(mav, "recent");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Integer actualMaxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        assertEquals((int) actualMaxPages, 1);

        Integer actualPage = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((int) actualPage, page);

    }
}
