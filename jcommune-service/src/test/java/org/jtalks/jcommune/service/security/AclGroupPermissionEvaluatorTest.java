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

import org.jtalks.common.model.dao.GroupDao;
import org.jtalks.common.model.entity.Group;
import org.jtalks.common.model.entity.User;
import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.model.permissions.JtalksPermission;
import org.jtalks.common.model.permissions.ProfilePermission;
import org.jtalks.common.security.acl.AclUtil;
import org.jtalks.common.security.acl.ExtendedMutableAcl;
import org.jtalks.common.security.acl.GroupAce;
import org.jtalks.common.security.acl.sids.JtalksSidFactory;
import org.jtalks.common.security.acl.sids.UserSid;
import org.jtalks.jcommune.model.entity.JCUser;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stanislav bashkirtsev
 */
public class AclGroupPermissionEvaluatorTest {
    @Mock
    private org.jtalks.common.security.acl.AclManager aclManager;
    @Mock
    private AclUtil aclUtil;
    @Mock
    private GroupDao groupDao;
    @Mock
    private JtalksSidFactory sidFactory;
    @Mock
    private ExtendedMutableAcl mutableAcl;
    @Mock
    Authentication authentication;

    private AclGroupPermissionEvaluator evaluator;
    private UserSid userSid;
    private ObjectIdentityImpl objectIdentity;
    private User user;

    private Long targetId = 1L;
    private String targetIdString = "1";
    private String targetType = "BRANCH";
    private String permission = "GeneralPermission.WRITE";

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        evaluator = new AclGroupPermissionEvaluator(aclManager, aclUtil, groupDao, sidFactory);
        objectIdentity = new ObjectIdentityImpl(targetType, targetId);
        Mockito.when(aclUtil.createIdentity(targetId, targetType)).thenReturn(objectIdentity);
        user = new JCUser("username", "email", "password");
        user.setId(1);
        userSid = new UserSid(user);
        Mockito.when(sidFactory.createPrincipal(authentication)).thenReturn(userSid);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    public void testHasPermissionForUserSidSuccessTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        aces.add(createAccessControlEntry(GeneralPermission.WRITE, true, userSid));
        aces.add(createAccessControlEntry(ProfilePermission.EDIT_PROFILE, true, userSid));
        aces.add(createAccessControlEntry(BranchPermission.CLOSE_TOPICS, true, userSid));
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, "GeneralPermission.WRITE"));
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, "ProfilePermission.EDIT_PROFILE"));
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, "BranchPermission.CLOSE_TOPICS"));
    }

    @Test
    public void testHasPermissionForGroupSidSuccessTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        controlEntries.add(createGroupAce(true));
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertTrue(evaluator.hasPermission(authentication, targetId, targetType, "GeneralPermission.WRITE"));
    }

    @Test
    public void testHasPermissionForUserSidNotSuccessTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        aces.add(createAccessControlEntry(GeneralPermission.WRITE, false, userSid));
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    @Test
    public void testHasPermissionForGroupSidNotSuccessTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        controlEntries.add(createGroupAce(false));
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, "GeneralPermission.WRITE"));
    }

    @Test
    public void testHasPermissionForUserSidEmptyTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, permission));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testHasPermissionForInvalidPermissionTest() throws Exception {
        List<AccessControlEntry> aces = new ArrayList<AccessControlEntry>();
        Mockito.when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
        Mockito.when(mutableAcl.getEntries()).thenReturn(aces);

        List<GroupAce> controlEntries = new ArrayList<GroupAce>();
        Mockito.when(aclManager.getGroupPermissionsOn(objectIdentity)).thenReturn(controlEntries);
        Assert.assertFalse(evaluator.hasPermission(authentication, targetId, targetType, "123"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
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

    private GroupAce createGroupAce( boolean isGranted) {
        GroupAce groupAce = Mockito.mock(GroupAce.class);
        Group group = Mockito.mock(Group.class);
        List<User> users = new ArrayList<User>();
        users.add(user);
        Mockito.when(group.getUsers()).thenReturn(users);
        Mockito.when(groupAce.getGroup(groupDao)).thenReturn(group);
        Mockito.when(groupAce.isGranting()).thenReturn(isGranted);
        return groupAce;
    }
}
