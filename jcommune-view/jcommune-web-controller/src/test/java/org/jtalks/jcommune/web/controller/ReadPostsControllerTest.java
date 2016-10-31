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


import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.LastReadPostService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.TopicFetchService;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.retry.policy.NeverRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class ReadPostsControllerTest {
    @Mock
    private BranchService branchService;
    @Mock
    private LastReadPostService lastReadPostService;
    @Mock
    private TopicFetchService topicFetchService;

    private ReadPostsController controller;
    RetryTemplate retryTemplate;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new NeverRetryPolicy());
        controller = new ReadPostsController(branchService, lastReadPostService, retryTemplate, topicFetchService);
    }
    
    @Test
    public void markAllForumAsReadFromRecentActivityShouldRedirectToRecentActivityPage() {
        String redirectUrl = controller.markAllForumAsReadFromRecentActivity();
        
        assertEquals(redirectUrl, "redirect:/topics/recent");
        verify(lastReadPostService).markAllForumAsReadForCurrentUser();
    }
    
    @Test
    public void markAllForumAsReadFromMainPageShouldRedirectToMainPage() {
        String redirectUrl = controller.markAllForumAsReadFromMainPage();
        
        assertEquals(redirectUrl, "redirect:/sections");
        verify(lastReadPostService).markAllForumAsReadForCurrentUser();
    }
    
    @Test
    public void markAllTopicsAsReadShouldMarkThemAndUpdatePage() throws NotFoundException {
        Long markedBranchId = 1L;
        Branch willBeMarkedBranch = new Branch("branch", "new branch");
        when(branchService.get(markedBranchId)).thenReturn(willBeMarkedBranch);
        
        String result = controller.markAllTopicsAsRead(markedBranchId);
        
        assertEquals(result, "redirect:/branches/" + String.valueOf(markedBranchId));
        verify(lastReadPostService).markAllTopicsAsRead(willBeMarkedBranch);
    }

    @Test
    public void markTopicAsReadByIdShouldMarkItAsRead() throws NotFoundException {
        Topic topicToRead = new Topic();
        when(topicFetchService.get(1L)).thenReturn(topicToRead);
        controller.markTopicPageAsReadById(1L, 10);
        verify(lastReadPostService).markTopicPageAsRead(topicToRead, 10);
    }
}
