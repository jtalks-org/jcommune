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
package org.jtalks.jcommune.service.security;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.common.security.acl.AclUtil;
import org.jtalks.common.security.acl.ExtendedMutableAcl;
import org.jtalks.common.security.acl.GroupAce;
import org.jtalks.common.security.acl.sids.JtalksSidFactory;
import org.jtalks.common.security.acl.sids.UserGroupSid;
import org.jtalks.common.security.acl.sids.UserSid;
import org.jtalks.jcommune.model.dao.UserDao;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author stanislav bashkirtsev
 */
public class AclGroupPermissionEvaluatorTest {
    @Mock
    private org.jtalks.common.security.acl.AclManager aclManager;
    @Mock
    private AclUtil aclUtil;
    @Mock
    private GroupDao commonGroupDao;
    @Mock
    private JtalksSidFactory sidFactory;
    @Mock
    private ExtendedMutableAcl mutableAcl;
    @Mock
    Authentication authentication;
    @Mock
    JdbcMutableAclService mutableAclService;
    @Mock
    MutableAcl acl;
    @Mock
    UserDao userDao;

    private AclGroupPermissionEvaluator evaluator;
    private UserGroupSid groupSid;
    private UserSid userSid;
    private ObjectIdentityImpl objectIdentity;
    private JCUser user;
    private Group group;

    private Long targetId = 1L;
    private String targetType = "BRANCH";
    private String permission = "BranchPermission.CREATE_POSTS";
    private BranchPermission generalPermission = BranchPermission.CREATE_POSTS;
    private BranchPermission someOtherPermission = BranchPermission.CLOSE_TOPICS;

    @BeforeMethod
    public void init() throws NotFoundException {
        MockitoAnnotations.initMocks(this);
        evaluator = new AclGroupPermissionEvaluator(aclManager, aclUtil, commonGroupDao,
                sidFactory, mutableAclService, userDao);
        objectIdentity = new ObjectIdentityImpl(targetType, targetId);
        Mockito.when(aclUtil.createIdentity(targetId, targetType)).thenReturn(objectIdentity);
        user = new JCUser("username", "email", "password");
        user.setId(1);
        userSid = new UserSid(user);
        groupSid = new UserGroupSid(targetId);
        group = Mockito.mock(Group.class);
        List<User> users = new ArrayList<User>();
        users.add(user);
        when(group.getUsers()).thenReturn(users);
        when(group.getId()).thenReturn(targetId);
        when(sidFactory.createPrincipal(authentication)).thenReturn(userSid);
        when(sidFactory.create(group)).thenReturn(groupSid);
        when(authentication.getPrincipal()).thenReturn(user);
        when(mutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(acl);
        when(userDao.get(user.getId())).thenReturn(user);
    }

    @Test
    public void testHasPermissionForUserSidSuccessTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        aces.add(createAccessControlEntry(generalPermission, true, userSid));
        aces.add(createAccessControlEntry(ProfilePermission.EDIT_OWN_PROFILE, true, userSid));
        aces.add(createAccessControlEntry(BranchPermission.CLOSE_TOPICS, true, userSid));
        Mockito.when(acl.getEntries()).thenReturn(aces);
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        String targetIdString = "1";
        Assert.assertTrue(evaluator.hasPermission(authentication, targetIdString, targetType, permission));
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, permission));
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, "ProfilePermission.EDIT_OWN_PROFILE"));
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, "BranchPermission.CLOSE_TOPICS"));
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, "GeneralPermission.READ"));
    }

    @Test
    public void testHasPermissionForPermissionOnGroupSuccessTest() throws Exception {
        setEnvForPermissionOnGroupTests(true);
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, permission));
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, "GeneralPermission.READ"));
    }

    @Test
    public void testHasPermissionForPermissionOnGroupNonExistentUserTest() throws Exception {
        setEnvForPermissionOnGroupTests(true);
        Long nonExistentId = -1L;
        user.setId(nonExistentId);
        when(userDao.get(nonExistentId)).thenReturn(null);
        Assert.assertFalse(evaluator.hasPermission(authentication, nonExistentId, targetType, permission));
        Assert.assertFalse(evaluator.hasPermission(authentication, nonExistentId, targetType, "GeneralPermission.READ"));
    }

    @Test
    public void testHasPermissionForPermissionOnGroupNotSuccessTest() throws Exception {
        setEnvForPermissionOnGroupTests(false);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    private void setEnvForPermissionOnGroupTests(boolean isGranted) {
        ObjectIdentity groupIdentity = new ObjectIdentityImpl("GROUP", targetId);
        Mockito.when(aclUtil.createIdentity(targetId, "GROUP")).thenReturn(groupIdentity);

        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        aces.add(createAccessControlEntry(generalPermission, isGranted, groupSid));
        Mockito.when(acl.getEntries()).thenReturn(aces);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(aclUtil.getAclFor(groupIdentity)).thenReturn(mutableAcl);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(groupIdentity)).thenReturn(controlEntries);

        List<Group> groups = new ArrayList<Group>();
        groups.add(group);
        ((JCUser) user).setGroups(groups);
    }

    @Test
    public void testHasPermissionForGroupSidSuccessTest() throws Exception {
        setEnvForGroupSidTests(true);
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    @Test
    public void testHasPermissionForGroupSidNotSuccessTest() throws Exception {
        setEnvForGroupSidTests(false);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    private void setEnvForGroupSidTests(boolean isGranted) {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);
        Mockito.when(acl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        controlEntries.add(createGroupAce(someOtherPermission, true));
        controlEntries.add(createGroupAce(someOtherPermission, false));
        controlEntries.add(createGroupAce(generalPermission, isGranted));
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
    }

    @Test
    public void testHasPermissionForUserSidNotSuccessTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        aces.add(createAccessControlEntry(generalPermission, false, userSid));
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);
        Mockito.when(acl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    @Test
    public void testHasPermissionForUserSidEmptyTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);
        Mockito.when(acl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHasPermissionForInvalidPermissionTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);
        Mockito.when(acl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, "123"));
    }

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testHasPermission() throws Exception {
        evaluator.hasPermission(authentication, targetId, permission);
    }

    private AccessControlEntry createAccessControlEntry(JtalksPermission permission, boolean isGranted, Sid sid) {
        AccessControlEntry accessControlEntry = Mockito.mock(AccessControlEntry.class);
        Mockito.when(accessControlEntry.getSid()).thenReturn(sid);
        Mockito.when(accessControlEntry.isGranting()).thenReturn(isGranted);
        Mockito.when(accessControlEntry.getPermission()).thenReturn(permission);
        return accessControlEntry;
    }

    private GroupAce createGroupAce(BranchPermission permission, boolean isGranted) {
        GroupAce groupAce = Mockito.mock(GroupAce.class);
        Group group = Mockito.mock(Group.class);
        List<User> users = new ArrayList<User>();
        users.add(user);
        Mockito.when(group.getUsers()).thenReturn(users);
        Mockito.when(groupAce.getGroup(commonGroupDao)).thenReturn(group);
        Mockito.when(groupAce.isGranting()).thenReturn(isGranted);
        Mockito.when(groupAce.getPermission()).thenReturn(permission);
        return groupAce;
    }
}
