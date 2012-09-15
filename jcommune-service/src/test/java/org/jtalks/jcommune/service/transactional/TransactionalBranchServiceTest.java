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

import static org.mockito.Matchers.anyLong;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicModificationService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
public class TransactionalBranchServiceTest {
    private long BRANCH_ID = 1L;
    final String BRANCH_NAME = "branch name";
    final String BRANCH_DESCRIPTION = "branch description";

    @Mock
    private BranchDao branchDao;
    @Mock
    private SectionDao sectionDao;
    @Mock
    private TopicDao topicDao;
    @Mock
    private PostDao postDao;
    @Mock
    private BranchService branchService;
    @Mock
    private TopicModificationService topicService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        branchService = new TransactionalBranchService(
                branchDao,
                sectionDao,
                topicDao,
                postDao,
                topicService);
    }

    @Test
    public void testGet() throws NotFoundException {
        Branch expectedBranch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(branchDao.get(BRANCH_ID)).thenReturn(expectedBranch);

        Branch branch = branchService.get(BRANCH_ID);

        assertEquals(branch, expectedBranch, "Branches aren't equal");
        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).get(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);
        branchService.get(BRANCH_ID);
    }

    @Test
    public void testGetBranchesInSection() throws NotFoundException {
        List<Branch> list = Collections.singletonList(new Branch(BRANCH_NAME, BRANCH_DESCRIPTION));
        when(sectionDao.isExist(Matchers.<Long>any())).thenReturn(true);
        when(branchDao.getBranchesInSection(anyLong())).thenReturn(list);

        List<Branch> result = branchService.getBranchesInSection(BRANCH_ID);
        assertEquals(list, result);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetBranchesInSectionFail() throws NotFoundException {
        when(sectionDao.isExist(Matchers.<Long>any())).thenReturn(false);
        branchService.getBranchesInSection(BRANCH_ID);
    }

    @Test
    public void testGetAllBranches() {
        List<Branch> list = Collections.singletonList(new Branch(BRANCH_NAME, BRANCH_DESCRIPTION));

        when(branchDao.getAllBranches()).thenReturn(list);

        List<Branch> result = branchService.getAllBranches();
        assertEquals(list, result);
    }
    
    @Test
    public void testFillStatisticInfo() {
        int expectedPostsCount = 10;
        int expectedTopicsCount = 20;
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        org.jtalks.common.model.entity.Branch commonBranch = branch;
        
        when(branchDao.getCountPostsInBranch(branch)).thenReturn(expectedPostsCount);
        when(topicDao.countTopics(branch)).thenReturn(expectedTopicsCount);
        
        branchService.fillStatisticInfo(Arrays.asList(commonBranch));
        
        assertEquals(branch.getTopicCount(), expectedTopicsCount,
                "Incorrect count of topics");
        assertEquals(branch.getPostCount(), expectedPostsCount,
                "Incorrect count of posts");
    }
    
    @Test
    public void testFillLastPostInLastUpdatedTopic() {
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        org.jtalks.common.model.entity.Branch commonBranch = branch;
        Topic dummyTopic = new Topic(null, null);
        Post expectedPost = new Post(null, null);
        
        when(topicDao.getLastUpdatedTopicInBranch(branch)).thenReturn(dummyTopic);
        when(postDao.getLastPostInTopic(dummyTopic)).thenReturn(expectedPost);
        
        branchService.fillLastPostInLastUpdatedTopic(Arrays.asList(commonBranch));
        Post actualPost = branch.getLastPostInLastUpdatedTopic();
        
        assertEquals(actualPost, expectedPost, "The last post in last updated topic is incorrect");
        verify(topicDao).getLastUpdatedTopicInBranch(commonBranch);
        verify(postDao).getLastPostInTopic(dummyTopic);
    }
    
    @Test
    public void testFillLastPostInLastUpdatedTopicInEmptyBranch() {
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        org.jtalks.common.model.entity.Branch commonBranch = branch;
        Topic nullTopic = null;
        
        when(topicDao.getLastUpdatedTopicInBranch(branch)).thenReturn(nullTopic);
        
        branchService.fillLastPostInLastUpdatedTopic(Arrays.asList(commonBranch));
        Post lastPostInLastUpdatedTopic = branch.getLastPostInLastUpdatedTopic();
        
        assertNull(lastPostInLastUpdatedTopic, "There should be null, because the branch is empty");
        verify(topicDao).getLastUpdatedTopicInBranch(commonBranch);
        verify(postDao, Mockito.never()).getLastPostInTopic(nullTopic);
    }
    
    @Test
    public void testGetBranch() throws NotFoundException {
        Branch expectedBranch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(branchDao.get(BRANCH_ID)).thenReturn(expectedBranch);

        Branch actualBranch = branchService.get(BRANCH_ID);

        assertEquals(actualBranch, expectedBranch, "Branches aren't equal");
        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).get(BRANCH_ID);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void testGetBranchWithIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        branchService.get(BRANCH_ID);
    }
    
    @Test
    public void testDeleteAllTopics() throws NotFoundException {
        Branch expectedBranch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        expectedBranch.addTopic(new Topic());
        expectedBranch.addTopic(new Topic());
        
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(branchDao.get(BRANCH_ID)).thenReturn(expectedBranch);
        
        Branch actualBranch = branchService.deleteAllTopics(BRANCH_ID);
        
        assertEquals(actualBranch, expectedBranch, "Branches aren't equal");
        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).get(BRANCH_ID);
        verify(topicService, times(2)).deleteTopicSilent(anyLong());
    }
    
    @Test
    public void testDeleteAllTopicsInEmptyBranch() throws NotFoundException {
        Branch expectedBranch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        when(branchDao.isExist(BRANCH_ID)).thenReturn(true);
        when(branchDao.get(BRANCH_ID)).thenReturn(expectedBranch);
        
        Branch actualBranch = branchService.deleteAllTopics(BRANCH_ID);
        
        assertEquals(actualBranch, expectedBranch, "Branches aren't equal");
        verify(branchDao).isExist(BRANCH_ID);
        verify(branchDao).get(BRANCH_ID);
        verify(topicService, times(0)).deleteTopicSilent(anyLong());
    }
    
    @Test(expectedExceptions=NotFoundException.class)
    public void testDeleteAllTopicsWithIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);
        
        branchService.deleteAllTopics(BRANCH_ID);
        
        assertTrue(false);
    }
}
