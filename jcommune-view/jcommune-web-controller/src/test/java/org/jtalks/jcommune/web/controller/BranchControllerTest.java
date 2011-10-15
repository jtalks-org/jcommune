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
import org.jtalks.jcommune.service.TopicService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.web.dto.Breadcrumb;
import org.jtalks.jcommune.web.dto.BreadcrumbBuilder;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionContext;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.ModelAndViewAssert.*;
import static org.testng.Assert.assertEquals;


/**
 * @author Kravchenko Vitaliy
 * @author Alexandre Teterin
 */
public class BranchControllerTest {
    private BranchService branchService;
    private TopicService topicService;
    private BranchController controller;
    private BreadcrumbBuilder breadcrumbBuilder;

    private static final DateTime now = new DateTime();

    @BeforeMethod
    public void init() {
        branchService = mock(BranchService.class);
        topicService = mock(TopicService.class);
        breadcrumbBuilder = mock(BreadcrumbBuilder.class);
        controller = new BranchController(branchService, topicService, breadcrumbBuilder);
    }

    @Test
    public void testTopicsInBranch() throws NotFoundException {
        long branchId = 1L;
        int page = 2;
        int pageSize = 5;
        int startIndex = page * pageSize - pageSize;
        //set expectations
        when(topicService.getTopicsInBranchCount(branchId)).thenReturn(10);
        when(topicService.getTopicRangeInBranch(branchId, startIndex, pageSize)).thenReturn(new ArrayList<Topic>());
        when(branchService.get(branchId)).thenReturn(new Branch("name"));
        when(breadcrumbBuilder.getForumBreadcrumb(branchService.get(branchId)))
                .thenReturn(new ArrayList<Breadcrumb>());

        //invoke the object under test
        ModelAndView mav = controller.show(branchId, page, pageSize);

        //check expectations
        verify(topicService).getTopicRangeInBranch(branchId, startIndex, pageSize);
        verify(topicService).getTopicsInBranchCount(branchId);
        verify(breadcrumbBuilder).getForumBreadcrumb(branchService.get(branchId));

        //check result
        assertViewName(mav, "topicList");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Long actualBranch = assertAndReturnModelAttributeOfType(mav, "branchId", Long.class);
        assertEquals((long) actualBranch, branchId);

        Integer actualMaxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        assertEquals((int) actualMaxPages, 2);

        Integer actualPage = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((int) actualPage, page);

        assertModelAttributeAvailable(mav, "breadcrumbList");

    }

    @Test
    public void testRecent() throws NotFoundException {
        int page = 2;
        int pageSize = 5;
        int startIndex = page * pageSize - pageSize;
        //set expectations
        when(topicService.getTopicsPastLastDayCount(now)).thenReturn(10);
        when(topicService.getAllTopicsPastLastDay(startIndex, pageSize, now)).thenReturn(new ArrayList<Topic>());


        //invoke the object under test
        ModelAndView mav = controller.show(page, pageSize, getFakeSession());

        //check expectations
        verify(topicService).getAllTopicsPastLastDay(startIndex, pageSize, now);
        verify(topicService).getTopicsPastLastDayCount(now);

        //check result
        assertViewName(mav, "recent");
        assertAndReturnModelAttributeOfType(mav, "topics", List.class);

        Integer actualMaxPages = assertAndReturnModelAttributeOfType(mav, "maxPages", Integer.class);
        assertEquals((int) actualMaxPages, 2);

        Integer actualPage = assertAndReturnModelAttributeOfType(mav, "page", Integer.class);
        assertEquals((int) actualPage, page);

    }

    private MockHttpSession getFakeSession() {
        return new MockHttpSession() {

            @Override
            public long getCreationTime() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String getId() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public long getLastAccessedTime() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public ServletContext getServletContext() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setMaxInactiveInterval(int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public HttpSessionContext getSessionContext() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Object getAttribute(String s) {
                return now;
            }

            @Override
            public Object getValue(String s) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Enumeration getAttributeNames() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String[] getValueNames() {
                return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void setAttribute(String s, Object o) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void putValue(String s, Object o) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void removeAttribute(String s) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void removeValue(String s) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void invalidate() {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public boolean isNew() {
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
