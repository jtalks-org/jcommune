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

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.Section;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.jcommune.model.dao.BranchDao;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.model.dao.TopicDao;
import org.jtalks.jcommune.model.dto.GroupsPermissions;
import org.jtalks.jcommune.model.dto.PermissionChanges;
import org.jtalks.jcommune.model.entity.*;
import org.jtalks.jcommune.service.BranchLastPostService;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.TopicModificationService;
import org.jtalks.jcommune.service.UserService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

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
    private GroupDao groupDao;
    @Mock
    private BranchService branchService;
    @Mock
    private TopicModificationService topicService;
    @Mock
    private UserService userService;
    @Mock
    private PermissionService permissionService;
    @Mock
    private BranchLastPostService lastPostService;

    private Topic topic;
    private Section section;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        branchService = new TransactionalBranchService(
                branchDao,
                sectionDao,
                topicDao,
                groupDao,
                topicService,
                permissionService,
                lastPostService);
        topic = null;
        section = null;
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
    public void getAvailableBranchesInSectionCheckPermission() throws NotFoundException {
        setUpGetAvailableBranchesInSection();
        when(permissionService.hasBranchPermission(section.getBranches().get(1).getId(),
                BranchPermission.VIEW_TOPICS)).thenReturn(true);

        List<Branch> result = branchService.getAvailableBranchesInSection(SECTION_ID, TOPIC_ID);
        assertEquals(result.size(), 1, "User shouldn't see branches without view permission.");
    }

    @Test
    public void getAvailableBranchesInSectionShouldRemoveTopicBranch() throws NotFoundException {
        setUpGetAvailableBranchesInSection();
        for (org.jtalks.common.model.entity.Branch branch : section.getBranches()) {
            when(permissionService.hasBranchPermission(branch.getId(), BranchPermission.VIEW_TOPICS)).thenReturn(true);
        }

        List<Branch> result = branchService.getAvailableBranchesInSection(SECTION_ID, TOPIC_ID);
        assertFalse(result.contains(topic.getBranch()), "Topic shouldn't be accessible for move to the same branch.");
    }

    private void setUpGetAvailableBranchesInSection() {
        topic = ObjectsFactory.getDefaultTopic();
        section = ObjectsFactory.getDefaultSectionWithBranches();
        Branch topicBranch = (Branch) section.getBranches().get(0);
        topicBranch.addTopic(topic);

        when(sectionDao.isExist(SECTION_ID)).thenReturn(true);
        when(sectionDao.get(SECTION_ID)).thenReturn(section);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
    }

    @Test(expectedExceptions = {NotFoundException.class})
    public void getAvailableBranchesInSectionWithIncorrectSectionId() throws NotFoundException {
        when(sectionDao.isExist(SECTION_ID)).thenReturn(false);
        branchService.getAvailableBranchesInSection(SECTION_ID, TOPIC_ID);
    }

    @Test
    public void getAllAvailableBranchesCheckPermission() {
        List<Section> sections = ObjectsFactory.getDefaultSectionListWithBranches();
        topic = ObjectsFactory.getDefaultTopic();
        Branch topicBranch = (Branch) sections.get(0).getBranches().get(0);
        topicBranch.addTopic(topic);

        when(sectionDao.getAll()).thenReturn(sections);
        when(topicDao.get(TOPIC_ID)).thenReturn(topic);
        when(permissionService.hasBranchPermission(sections.get(0).getBranches().get(1).getId(),
                BranchPermission.VIEW_TOPICS)).thenReturn(true);
        when(permissionService.hasBranchPermission(sections.get(1).getBranches().get(2).getId(),
                BranchPermission.VIEW_TOPICS)).thenReturn(true);

        List<Branch> result = branchService.getAllAvailableBranches(TOPIC_ID);
        assertEquals(result.size(), 2, "User shouldn't see branches without view permission.");
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

    @Test(expectedExceptions = NotFoundException.class)
    public void getPermissionsShouldThrowExceptionWhenBranchDoesNotExist() throws Exception {
        when(branchDao.isExist(42L)).thenReturn(false);

        branchService.getPermissionsFor(0, 42L);
    }

    @Test
    public void getPermissionsShouldReturnPermissionsWhenBranchExist() throws Exception {
        long branchId = 42;
        GroupsPermissions<BranchPermission> expectedPermissions = new GroupsPermissions<>();

        Branch expectedBranch = new Branch("name", "description");
        when(branchDao.isExist(branchId)).thenReturn(true);
        when(branchDao.get(branchId)).thenReturn(expectedBranch);
        doReturn(expectedPermissions).when(permissionService).getPermissionsFor(expectedBranch);

        GroupsPermissions<BranchPermission> permissions = branchService.getPermissionsFor(0, branchId);
        assertEquals(permissions, expectedPermissions);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void getPermissionGroupsShouldThrowExceptionWhenBranchDoesNotExist() throws Exception {
        when(branchDao.isExist(42L)).thenReturn(false);

        branchService.getPermissionGroupsFor(0, 42L, true, BranchPermission.CLOSE_TOPICS);
    }

    @Test
    public void getPermissionGroupsShouldReturnAllowedPermissionGroupsWhenBranchExist() throws Exception {
        long branchId = 42;
        BranchPermission permission = BranchPermission.CLOSE_TOPICS;
        GroupsPermissions<BranchPermission> expectedPermissions = new GroupsPermissions<>();
        expectedPermissions.addAllowed(permission, new Group("1"));

        Branch expectedBranch = new Branch("name", "description");
        when(branchDao.isExist(branchId)).thenReturn(true);
        when(branchDao.get(branchId)).thenReturn(expectedBranch);
        doReturn(expectedPermissions).when(permissionService).getPermissionsFor(expectedBranch);

        List<Group> result = branchService.getPermissionGroupsFor(0, branchId, true, permission);
        assertEquals(result, expectedPermissions.getAllowed(permission));
    }

    @Test
    public void getPermissionGroupsShouldReturnRestrictedPermissionGroupsWhenBranchExist() throws Exception {
        long branchId = 42;
        BranchPermission permission = BranchPermission.CLOSE_TOPICS;
        GroupsPermissions<BranchPermission> expectedPermissions = new GroupsPermissions<>();
        expectedPermissions.addRestricted(permission, new Group("1"));

        Branch expectedBranch = new Branch("name", "description");
        when(branchDao.isExist(branchId)).thenReturn(true);
        when(branchDao.get(branchId)).thenReturn(expectedBranch);
        doReturn(expectedPermissions).when(permissionService).getPermissionsFor(expectedBranch);

        List<Group> result = branchService.getPermissionGroupsFor(0, branchId, false, permission);
        assertEquals(result, expectedPermissions.getRestricted(permission));
    }

    @Test
    public void changeBranchPermissionsShouldChangeGrantsWhenAllowedIsTrue() throws Exception{
        long branchId = 42;
        PermissionChanges changes = new PermissionChanges(BranchPermission.CLOSE_TOPICS, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        Branch expectedBranch = new Branch("name", "description");

        when(branchDao.isExist(branchId)).thenReturn(true);
        when(branchDao.get(branchId)).thenReturn(expectedBranch);

        branchService.changeBranchPermissions(0, branchId, true, changes);
        verify(permissionService).changeGrants(expectedBranch, changes);
    }

    @Test
    public void changeBranchPermissionsShouldChangeRestrictionsWhenAllowedIsFalse() throws Exception {
        long branchId = 42;
        PermissionChanges changes = new PermissionChanges(BranchPermission.CLOSE_TOPICS, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        Branch expectedBranch = new Branch("name", "description");

        when(branchDao.isExist(branchId)).thenReturn(true);
        when(branchDao.get(branchId)).thenReturn(expectedBranch);

        branchService.changeBranchPermissions(0, branchId, false, changes);
        verify(permissionService).changeRestrictions(expectedBranch, changes);
    }

    @Test(expectedExceptions = NotFoundException.class)
    public void changeBranchPermissionsShouldThrowExceptionWhenBranchNotFound() throws Exception{
        long branchId = 42;
        when(branchDao.isExist(branchId)).thenReturn(false);
        PermissionChanges changes = new PermissionChanges(BranchPermission.CLOSE_TOPICS, Collections.EMPTY_LIST, Collections.EMPTY_LIST);

        branchService.changeBranchPermissions(0, branchId, false, changes);
    }
}
