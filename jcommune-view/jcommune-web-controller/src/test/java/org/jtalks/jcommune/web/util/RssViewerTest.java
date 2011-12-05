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

package org.jtalks.jcommune.web.util;

import com.sun.syndication.feed.rss.Channel;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SecurityService;
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.web.controller.BranchController;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RssViewerTest {
    private BranchController branchController;
    private RssViewer rssViewer;
    private MockHttpSession session;
    private Channel channel;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private TopicService topicService;
    private static final DateTime now = new DateTime();

    private BranchService branchService;
    private BreadcrumbBuilder breadcrumbBuilder;

    @BeforeMethod
    protected void setUp() {
       rssViewer = mock(RssViewer.class);
       session = new MockHttpSession();
       channel = new Channel();
       topicService = mock(TopicService.class);
       request = new MockHttpServletRequest();
       response = new MockHttpServletResponse();
       channel = new Channel();

       branchService = mock(BranchService.class);
        topicService = mock(TopicService.class);
        SecurityService securityService = mock(SecurityService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        branchController = new BranchController(branchService, topicService, securityService, breadcrumbBuilder);
    }

    @Test
    public void testBuildFeedMetadata() throws Exception {

        session.setAttribute("lastlogin", now);
        Map model = branchController.recentTopicsPage(1,session).getModel();
        when(model.get("topics")).thenReturn(new ArrayList<Topic>());

        rssViewer.buildFeedItems(model,request,response);
        rssViewer.buildFeedMetadata(model,channel,request);

        verify(rssViewer).buildFeedMetadata(model, channel, request);
        verify(rssViewer).buildFeedItems(model,request,response);

    }

}
