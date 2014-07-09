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

import org.jtalks.common.service.security.SecurityContextFacade;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.*;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.jtalks.jcommune.web.dto.BranchDto;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.util.BreadcrumbBuilder;
import org.jtalks.jcommune.web.util.ForumStatisticsProvider;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;


/**
 * @author Kravchenko Vitaliy
 * @author Alexandre Teterin
 * @author Evdeniy Naumenko
 * @author Eugeny Batov
 */
public class BranchControllerTest {
    @Mock
    private BranchService branchService;
    @Mock
    private TopicFetchService topicFetchService;
    @Mock
    private BreadcrumbBuilder breadcrumbBuilder;
    @Mock
    private LocationService locationServiceImpl;
    @Mock
    private ForumStatisticsProvider forumStatisticsProvider;
    @Mock
    private LastReadPostService lastReadPostService;
    @Mock
    private UserService userService;
    @Mock
    private PostService postService;
    @Mock
    private PermissionEvaluator permissionEvaluator;
    @Mock
    private SecurityContextFacade securityContextFacade;

    private BranchController controller;

    @BeforeMethod
    public void init() {
        initMocks(this);
        controller = new BranchController(
                branchService,
                topicFetchService,
                lastReadPostService,
                userService,
                breadcrumbBuilder,
                locationServiceImpl,
                postService, permissionEvaluator, securityContextFacade);
    }

    @Test
    public void showPage() throws NotFoundException {
        long branchId = 1L;
        String page = "2";
        Branch branch = new Branch("name", "description");
        branch.setId(branchId);
        Pageable pageRequest = new PageRequest(2, 5);
        Page<Topic> topicsPage = new PageImpl<Topic>(Collections.<Topic> emptyList(), pageRequest, 0);
        //set expectations
        when(branchService.get(branchId)).thenReturn(branch);
        when(topicFetchService.getTopics(branch, page)).thenReturn(topicsPage);
        when(breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)))
                .thenReturn(new ArrayList<Breadcrumb>());
        when(forumStatisticsProvider.getOnlineRegisteredUsers()).thenReturn(new ArrayList<Object>());

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContextFacade.getContext()).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        //invoke the object under test
        ModelAndView mav = controller.showPage(branchId, page);

        //check expectations
        verify(breadcrumbBuilder).getForumBreadcrumb(branchService.get(branchId));

        //check result
        assertViewName(mav, "topic/topicList");

        Branch actualBranch = assertAndReturnModelAttributeOfType(mav, "branch", Branch.class);
        assertEquals(actualBranch.getId(), branchId);

        @SuppressWarnings("unchecked")
        Page<Topic> actualTopicsPage = 
            (Page<Topic>) assertAndReturnModelAttributeOfType(mav, "topicsPage", Page.class);
        assertEquals(actualTopicsPage, topicsPage);
        
        assertModelAttributeAvailable(mav, "breadcrumbList");
    }

    @Test
    public void recentTopicsPage() throws NotFoundException {
        String page = "1";
        Page<Topic> topicsPage = new PageImpl<Topic>(new ArrayList<Topic>());
        //set expectations
        when(topicFetchService.getRecentTopics(page)).thenReturn(topicsPage);

        //invoke the object under test
        ModelAndView mav = controller.recentTopicsPage(page);

        //check expectations
        verify(topicFetchService).getRecentTopics(page);

        //check result
        assertViewName(mav, "topic/recent");
        assertAndReturnModelAttributeOfType(mav, "topicsPage", Page.class);
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);
    }

    @Test
    public void unansweredTopicsPage() {
        String page = "1";
        Page<Topic> topicsPage = new PageImpl<Topic>(new ArrayList<Topic>());
        //set expectations
        when(topicFetchService.getUnansweredTopics(page)).thenReturn(topicsPage);

        //invoke the object under test
        ModelAndView mav = controller.unansweredTopicsPage(page);

        //check expectations
        verify(topicFetchService).getUnansweredTopics(page);

        //check result
        assertViewName(mav, "topic/unansweredTopics");
        assertAndReturnModelAttributeOfType(mav, "topicsPage", Page.class);
    }

    @Test
    public void testViewList() throws NotFoundException {
        long branchId = 1L;
        String page = "2";
        Pageable pageRequest = new PageRequest(2, 5);
        Page<Topic> topicsPage = new PageImpl<Topic>(Collections.<Topic> emptyList(), pageRequest, 0);
        Branch branch = new Branch("name", "description");
        branch.setId(branchId);
        //set expectations
        when(branchService.get(branchId)).thenReturn(branch);
        when(breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)))
                .thenReturn(new ArrayList<Breadcrumb>());
        when(forumStatisticsProvider.getOnlineRegisteredUsers()).thenReturn(new ArrayList<Object>());
        when(topicFetchService.getTopics(branch, page)).thenReturn(topicsPage);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContextFacade.getContext()).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        ModelAndView mav = controller.showPage(branchId, page);

        List<?> actualViewList = assertAndReturnModelAttributeOfType(mav, "viewList", List.class);
        assertEquals(actualViewList, new ArrayList<String>());
    }

    @Test
    public void testGetBranchesFromSection() throws NotFoundException {
        long sectionId = 1L;
        List<Branch> branches = new ArrayList<Branch>();
        Branch branch = createDefaultBranch();
        branches.add(branch);
        when(branchService.getAvailableBranchesInSection(sectionId,
                branch.getTopics().get(0).getId())).thenReturn(branches);

        BranchDto[] branchDtoArray = controller.getBranchesFromSection(sectionId, branch.getTopics().get(0).getId());

        assertEquals(branchDtoArray.length, branches.size());
        assertEquals(branchDtoArray[0].getId(), branch.getId());
        assertEquals(branchDtoArray[0].getName(), branch.getName());
    }

    @Test
    public void testGetAllBranches() throws NotFoundException {
        List<Branch> branches = new ArrayList<Branch>();
        Branch branch = createDefaultBranch();
        branches.add(branch);
        when(branchService.getAllAvailableBranches(branch.getTopics().get(0).getId())).thenReturn(branches);

        BranchDto[] branchDtoArray = controller.getAllBranches(branch.getTopics().get(0).getId());

        assertEquals(branchDtoArray.length, branches.size());
        assertEquals(branchDtoArray[0].getId(), branch.getId());
        assertEquals(branchDtoArray[0].getName(), branch.getName());
    }

    private Branch createDefaultBranch(){
        long branchId = 1L;
        long topicId = 1L;
        JCUser user = new JCUser("username", "email", "password");
        Topic topic = new Topic(user, "Topic title");
        topic.setId(topicId);
        Branch branch = new Branch("name", "description");
        branch.setId(branchId);
        branch.addTopic(topic);
        return branch;
    }

}
