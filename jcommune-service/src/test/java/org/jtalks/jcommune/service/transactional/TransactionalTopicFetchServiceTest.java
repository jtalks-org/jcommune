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
package org.jtalks.jcommune.service.transactional;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dao.search.TopicSearchDao;
import org.jtalks.jcommune.model.dto.PageRequest;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.ComponentService;
import org.jtalks.jcommune.service.TopicFetchService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class TransactionalTopicFetchServiceTest {

    @Mock
    private TopicDao topicDao;
    @Mock
    private UserService userService;
    @Mock
    private TopicSearchDao searchDao;

    private TopicFetchService topicFetchService;

    private JCUser user;
    @Mock
    private ComponentService componentService;

    @BeforeMethod
    public void init(){
        initMocks(this);
        topicFetchService = new TransactionalTopicFetchService(topicDao, componentService, userService, searchDao);
        user = new JCUser("username", "email@mail.com", "password");
        when(userService.getCurrentUser()).thenReturn(user);
    }
    
    @Test
    public void testGetTopic() throws NotFoundException {
        Topic expectedTopic = new Topic(user, "title");
        when(topicDao.isExist(999L)).thenReturn(true);
        when(topicDao.get(999L)).thenReturn(expectedTopic);

        int viewsCount = expectedTopic.getViews();

        Topic actualTopic = topicFetchService.get(999L);

        assertEquals(actualTopic.getViews(), viewsCount + 1);
        assertEquals(actualTopic, expectedTopic, "Topics aren't equal");
        verify(topicDao).isExist(999L);
        verify(topicDao).get(999L);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetTopicWithIncorrectId() throws NotFoundException {
        when(topicDao.isExist(333L)).thenReturn(false);

        topicFetchService.get(333L);
    }

    @Test
    public void testGetAllTopicsPastLastDay() throws NotFoundException {
        String pageNumber = "1";
        int pageSize = 20;
        List<Topic> expectedList = Collections.nCopies(2, new Topic(user, "title"));
        Page<Topic> expectedPage = new PageImpl<>(expectedList);
        when(topicDao.getTopicsUpdatedSince(Matchers.<DateTime>any(), Matchers.<PageRequest>any(),eq(user)))
                .thenReturn(expectedPage);
        user.setPageSize(pageSize);
        when(userService.getCurrentUser()).thenReturn(user);

        Page<Topic> actualPage = topicFetchService.getRecentTopics(pageNumber);

        assertNotNull(actualPage);
        assertEquals(expectedPage, actualPage);
        verify(topicDao).getTopicsUpdatedSince(Matchers.<DateTime>any(), Matchers.<PageRequest>any(),
                eq(user));
    }

    @Test
    public void testGetUnansweredTopics() {
        String pageNumber = "1";
        int pageSize = 20;
        List<Topic> expectedList = Collections.nCopies(2, new Topic(user, "title"));
        Page<Topic> expectedPage = new PageImpl<>(expectedList);
        when(topicDao.getUnansweredTopics(Matchers.<PageRequest>any(),eq(user)))
                .thenReturn(expectedPage);
        user.setPageSize(pageSize);
        when(userService.getCurrentUser()).thenReturn(user);

        Page<Topic> actualPage = topicFetchService.getUnansweredTopics(pageNumber);
        assertNotNull(actualPage);
        assertEquals(actualPage, expectedPage);
    }

    @Test
    public void testGetTopics() {
        String pageNumber = "50";
        Branch branch = createBranch();
        Page<Topic> expectedPage = new PageImpl<>(Collections.<Topic>emptyList());

        JCUser currentUser = new JCUser("current", null, null);
        currentUser.setPageSize(50);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(topicDao.getTopics(
                Matchers.any(Branch.class), Matchers.any(PageRequest.class)))
                .thenReturn(expectedPage);

        Page<Topic> actualPage = topicFetchService.getTopics(branch, pageNumber);

        assertEquals(actualPage, expectedPage, "Service returned incorrect data for one page of topics");
        verify(topicDao).getTopics(
                Matchers.any(Branch.class), Matchers.any(PageRequest.class));
    }

    private Branch createBranch() {
        Branch branch = new Branch("branch name", "branch description");
        branch.setId(1L);
        branch.setUuid("uuid");
        return branch;
    }


    @Test(dataProvider = "parameterSearchPostsWithEmptySearchPhrase")
    public void testSearchPostsWithEmptySearchPhrase(String phrase) {
        Page<Topic> searchResultPage = topicFetchService.searchByTitleAndContent(phrase, "50");

        Assert.assertTrue(!searchResultPage.hasContent(), "The search result must be empty.");
    }

    @DataProvider(name = "parameterSearchPostsWithEmptySearchPhrase")
    public Object[][] parameterSearchPostsWithEmptySearchPhrase() {
        return new Object[][] {
                {StringUtils.EMPTY},
                {null}
        };
    }

    private Component setupComponentMock() {
        Component component = new Component();
        component.setId(1L);
        when(componentService.getComponentOfForum()).thenReturn(component);
        return component;
    }

    @Test
    public void testRebuildIndex() {
        setupComponentMock();
        topicFetchService.rebuildSearchIndex();

        Mockito.verify(searchDao).rebuildIndex();
    }

    @Test
    public void getTopicSilentlyShouldNotCallSaveOrUpdate() throws Exception{
        Topic expectedTopic = new Topic(user, "title");
        when(topicDao.isExist(999L)).thenReturn(true);
        when(topicDao.get(999L)).thenReturn(expectedTopic);

        Topic result = topicFetchService.getTopicSilently(999L);

        assertEquals(result, expectedTopic);
        verify(topicDao, never()).saveOrUpdate(any(Topic.class));
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getTopicSilentlyShouldThrowExceptionIfTopicNotFound() throws Exception{
        when(topicDao.isExist(333L)).thenReturn(false);

        topicFetchService.getTopicSilently(333L);
    }
}
