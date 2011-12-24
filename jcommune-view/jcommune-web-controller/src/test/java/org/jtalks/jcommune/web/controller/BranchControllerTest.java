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
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationServiceImpl;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.ForumStatisticsProvider;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;


/**
 * @author Kravchenko Vitaliy
 * @author Alexandre Teterin
 * @author Evdeniy Naumenko
 * @author Eugeny Batov
 */
public class BranchControllerTest {
    private BranchService branchService;
    private TopicService topicService;
    private BranchController controller;
    private BreadcrumbBuilder breadcrumbBuilder;
    private LocationServiceImpl locationServiceImpl;
    private ForumStatisticsProvider forumStatisticsProvider;


    private static final DateTime now = new DateTime();

    @BeforeMethod
    public void init() {
        locationServiceImpl = mock(LocationServiceImpl.class);
        branchService = mock(BranchService.class);
        topicService = mock(TopicService.class);
        SecurityService securityService = mock(SecurityService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        forumStatisticsProvider = mock(ForumStatisticsProvider.class);
        controller = new BranchController(branchService, topicService,
                securityService, breadcrumbBuilder,
                locationServiceImpl);
    }

    @Test
    public void showPage() throws NotFoundException {
        User user = new User("", "", "");
        Map map = new HashMap<User, String>();
        map.put(user, "");
        long branchId = 1L;
        int page = 2;
        boolean pagingEnabled = true;
        Branch branch = new Branch("name");
        branch.setId(branchId);
        //set expectations
        when(branchService.get(branchId)).thenReturn(branch);
        when(breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)))
                .thenReturn(new ArrayList<Breadcrumb>());
        when(locationServiceImpl.getRegisterUserMap()).thenReturn(map);
        when(forumStatisticsProvider.getOnlineRegisteredUsers()).thenReturn(new ArrayList<Object>());

        //invoke the object under test
        ModelAndView mav = controller.showPage(branchId, page, pagingEnabled);

        //check expectations
        verify(breadcrumbBuilder).getForumBreadcrumb(branchService.get(branchId));

        //check result
        assertViewName(mav, "topicList");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Branch actualBranch = assertAndReturnModelAttributeOfType(mav, "branch", Branch.class);
        assertEquals(actualBranch.getId(), branchId);

        Pagination pagination = assertAndReturnModelAttributeOfType(mav, "pagination", Pagination.class);
        assertEquals(pagination.getPage().intValue(), page);
        assertModelAttributeAvailable(mav, "breadcrumbList");

    }

    @Test
    public void recentTopicsPage() throws NotFoundException {
        int page = 2;
        //set expectations
        when(topicService.getRecentTopics(now)).thenReturn(new ArrayList<Topic>());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("lastlogin", now);

        //invoke the object under test
        ModelAndView mav = controller.recentTopicsPage(page, session);

        //check expectations
        verify(topicService).getRecentTopics(now);

        //check result
        assertViewName(mav, "recent");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Pagination pagination = assertAndReturnModelAttributeOfType(mav, "pagination", Pagination.class);
        assertEquals(pagination.getMaxPages(), 1);
        assertEquals(pagination.getPage().intValue(), page);

    }

    @Test
    public void unansweredTopicsPage() {
        int page = 1;
        //set expectations
        when(topicService.getUnansweredTopics()).thenReturn(new ArrayList<Topic>());

        //invoke the object under test
        ModelAndView mav = controller.unansweredTopicsPage(page);

        //check expectations
        verify(topicService).getUnansweredTopics();

        //check result
        assertViewName(mav, "unansweredTopics");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Pagination pagination = assertAndReturnModelAttributeOfType(mav, "pagination", Pagination.class);
        assertEquals(pagination.getMaxPages(), 1);
        assertEquals(pagination.getPage().intValue(), page);
    }

    @Test
    public void testViewList() throws NotFoundException {
        User user = new User("", "", "");
        Map map = new HashMap<User, String>();
        map.put(user, "");
        long branchId = 1L;
        int page = 2;
        boolean pagingEnabled = true;
        Branch branch = new Branch("name");
        branch.setId(branchId);
        //set expectations
        when(branchService.get(branchId)).thenReturn(branch);
        when(breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)))
                .thenReturn(new ArrayList<Breadcrumb>());
        when(locationServiceImpl.getRegisterUserMap()).thenReturn(map);
        when(forumStatisticsProvider.getOnlineRegisteredUsers()).thenReturn(new ArrayList<Object>());

        ModelAndView mav = controller.showPage(branchId, page, pagingEnabled);

        List<String> actualViewList = assertAndReturnModelAttributeOfType(mav, "viewList", List.class);
        assertEquals(actualViewList, new ArrayList<String>());
    }
}
