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

import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicModificationService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This test class is intended to test all topic-related forum branch facilities
 *
 * @author Kravchenko Vitaliy
 * @author Kirill Afonin
 */
public class TransactionalBranchServiceTest {
    private static final long BRANCH_ID = 1L;
    private static final String BRANCH_NAME = "branch name";
    private static final String BRANCH_DESCRIPTION = "branch description";
    private static final long SECTION_ID = 1L;
    private static final long TOPIC_ID = 1L;

    @Mock
    private BranchDao branchDao;
    @Mock
    private SectionDao sectionDao;
    @Mock
    private TopicDao topicDao;
    @Mock
    private BranchService branchService;
    @Mock
    private TopicModificationService topicService;
    @Mock
    private UserService userService;
    @Mock
    private PermissionService permissionService;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        branchService = new TransactionalBranchService(
                branchDao,
                sectionDao,
                topicDao,
                topicService,
                permissionService);
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
    public void getBranchesInSectionShouldRemoveTopicBranch() throws NotFoundException {
        JCUser user = new JCUser("username", "email", "password");
        user.getGroups().add(new Group("Registered Users"));
        List<Branch> branches = createBranchList(user);

        Section section = new Section("section");
        for (Branch branch : branches) {
            section.addOrUpdateBranch(branch);
            when(permissionService.hasBranchPermission(branch.getId(), BranchPermission.VIEW_TOPICS)).thenReturn(true);
        }

        Topic topic = branches.get(0).getTopics().get(0);
        when(userService.getCurrentUser()).thenReturn(user);
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(section);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        List<Branch> result = branchService.getAvailableBranchesInSection(SECTION_ID, TOPIC_ID);
        assertFalse(result.contains(topic.getBranch()), "Topic shouldn't be accessible for move to the same branch.");
    }

    @Test
    public void getBranchesInSectionCheckPermission() throws NotFoundException {
        JCUser user = new JCUser("username", "email", "password");
        List<Branch> branches = createBranchList(user);

        Section section = new Section("section");
        for (Branch branch : branches) {
            section.addOrUpdateBranch(branch);
        }

        Topic topic = branches.get(0).getTopics().get(0);
        when(permissionService.hasBranchPermission(branches.get(1).getId(),
                BranchPermission.VIEW_TOPICS)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);
        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(section);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);

        List<Branch> result = branchService.getAvailableBranchesInSection(SECTION_ID, TOPIC_ID);
        assertEquals(result.size(), 1, "User shouldn't see branches without view permission.");
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void getBranchesInSectionWithIncorrectSectionId() throws NotFoundException {
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);
        branchService.getAvailableBranchesInSection(SECTION_ID, TOPIC_ID);
    }

    @Test
    public void testGetAllAvailableBranches() {
        JCUser user = new JCUser("username", "email", "password");
        user.getGroups().add(new Group("Registered Users"));
        List<Branch> branches = createBranchList(user);

        when(userService.getCurrentUser()).thenReturn(user);
        when(branchDao.getAllAvailableBranches(user)).thenReturn(branches);
        when(topicDao.get(TOPIC_ID)).thenReturn(branches.get(0).getTopics().get(0));

        List<Branch> result = branchService.getAllAvailableBranches(TOPIC_ID);
        assertEquals(result.size(), branches.size() - 1, "Topic shouldn't be accessible for move to the same branch.");
    }

    @Test
    public void testGetAllAvailableBranchesUserHasNotAnyPermissions() {
        JCUser user = new JCUser("username", "email", "password");
        createBranchList(user);
        when(userService.getCurrentUser()).thenReturn(user);

        List<Branch> result = branchService.getAllAvailableBranches(TOPIC_ID);
        assertEquals(result.size(), 0, "User shouldn't see any branch without permissions.");
    }

    private List<Branch> createBranchList(JCUser user){
        List<Branch> branches = ObjectsFactory.getDefaultBranchList();
        Topic topic = ObjectsFactory.getTopic(user, 1);
        topic.setBranch(branches.get(0));
        branches.get(0).addTopic(topic);
        return branches;
    }

    @Test
    public void testFillStatisticInfoToRegisteredUser() {
        int expectedPostsCount = 10;
        int expectedTopicsCount = 20;
        boolean expectedUnreadPostsCount = true;
        JCUser user = new JCUser("username", "email", "password");
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        org.jtalks.common.model.entity.Branch commonBranch = branch;

        when(branchDao.getCountPostsInBranch(branch)).thenReturn(expectedPostsCount);
        when(topicDao.countTopics(branch)).thenReturn(expectedTopicsCount);
        when(userService.getCurrentUser()).thenReturn(user);
        //TODO Was removed till milestone 2 due to performance issues
//        when(branchDao.isUnreadPostsInBranch(branch, user)).thenReturn(expectedUnreadPostsCount);

        branchService.fillStatisticInfo(Arrays.asList(commonBranch));

        assertEquals(branch.getTopicCount(), expectedTopicsCount,
                "Incorrect count of topics");
        assertEquals(branch.getPostCount(), expectedPostsCount,
                "Incorrect count of posts");
//        assertEquals(branch.isUnreadPosts(), expectedUnreadPostsCount,
//                "Incorrect unread posts state");
    }

    @Test
    public void testFillStatisticInfoToAnnonumous() {
        int expectedPostsCount = 10;
        int expectedTopicsCount = 20;
        boolean expectedUnreadPostsCount = true;
        JCUser user = new AnonymousUser();
        Branch branch = new Branch(BRANCH_NAME, BRANCH_DESCRIPTION);
        org.jtalks.common.model.entity.Branch commonBranch = branch;

        when(branchDao.getCountPostsInBranch(branch)).thenReturn(expectedPostsCount);
        when(topicDao.countTopics(branch)).thenReturn(expectedTopicsCount);
        when(userService.getCurrentUser()).thenReturn(user);
        //TODO fWas removed till milestone 2 due to performance issues
//        when(branchDao.isUnreadPostsInBranch(branch, user)).thenReturn(expectedUnreadPostsCount);

        branchService.fillStatisticInfo(Arrays.asList(commonBranch));

        assertEquals(branch.getTopicCount(), expectedTopicsCount,
                "Incorrect count of topics");
        assertEquals(branch.getPostCount(), expectedPostsCount,
                "Incorrect count of posts");
//        verify(branchDao, times(0)).isUnreadPostsInBranch(branch, user);
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

    @Test(expectedExceptions = NotFoundException.class)
    public void testDeleteAllTopicsWithIncorrectId() throws NotFoundException {
        when(branchDao.isExist(BRANCH_ID)).thenReturn(false);

        branchService.deleteAllTopics(BRANCH_ID);

        assertTrue(false);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void changeBranchInfoRequestShouldThrowExceptionWhenBranchDoesNotExist() throws NotFoundException {
        long branchId = 42;
        when(branchDao.isExist(branchId)).thenReturn(false);

        branchService.changeBranchInfo(0, branchId, "", "");
    }

}
