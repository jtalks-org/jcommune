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

import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;

import static com.google.common.collect.Lists.newArrayList;
import static org.jgroups.util.Util.assertTrue;
import static org.mockito.Matchers.eq;
import static org.testng.Assert.assertNotNull;

/**
 * @author stanislav bashkirtsev
 * @author Vyacheslav Zhivaev
 */
//@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
//@Transactional
public class PermissionManagerTest extends AbstractTransactionalTestNGSpringContextTests {
//    @Mock
//    private GroupDao groupDao;
//    @Mock
//    private AclManager aclManager;
//    @Mock
//    private AclUtil aclUtil;
//    @Mock
//    private PermissionManager manager;
//    @Autowired
//    private SessionFactory sessionFactory;
//    private Session session;
//
//    private List<Group> groups;
//    private List<GroupAce> groupAces;
//    private List<JtalksPermission> permissions;
//
//    @Deprecated
//    public static Group randomGroup(long id) {
//        Group group = new Group(RandomStringUtils.randomAlphanumeric(15), RandomStringUtils.randomAlphanumeric(20));
//        group.setId(id);
//        return group;
//    }
//
//    @Deprecated
//    public static Group getGroupWithId(List<Group> groups, long id) {
//        for (Group group : groups) {
//            if (group.getId() == id) {
//                return group;
//            }
//        }
//        return null;
//    }
//
//    /**
//     * Mockito answer for {@link org.jtalks.jcommune.model.dao.GroupDao#get(Long)} which return group from defined group list.
//     *
//     * @author Vyacheslav Zhivaev
//     */
//    class GroupDaoAnswer implements Answer<Group> {
//
//        private final List<Group> groups;
//
//        public GroupDaoAnswer(List<Group> groups) {
//            this.groups = groups;
//        }
//
//        /**
//         * {@inheritDoc}
//         */
//        @Override
//        public Group answer(InvocationOnMock invocation) throws Throwable {
//            long id = (Long) invocation.getArguments()[0];
//            return PermissionManagerTest.getGroupWithId(groups, id);
//        }
//
//    }
//
//    @BeforeMethod
//    public void beforeMethod() {
//        MockitoAnnotations.initMocks(this);
//
//        groups = Lists.newArrayList();
//        permissions = Lists.newArrayList();
//        groupAces = Lists.newArrayList();
//
//        session = sessionFactory.getCurrentSession();
//        PersistedObjectsFactory.setSession(session);
//
//        Long targetId = 1L;
//        String targetType = "BRANCH";
//        ObjectIdentityImpl objectIdentity = new ObjectIdentityImpl(targetType, targetId);
//        when(aclUtil.createIdentityFor(any(Entity.class))).thenReturn(objectIdentity);
//        ExtendedMutableAcl mutableAcl = mock(ExtendedMutableAcl.class);
//        List<AccessControlEntry> controlEntries = new ArrayList<AccessControlEntry>();
//        when(mutableAcl.getEntries()).thenReturn(controlEntries);
//        when(aclUtil.getAclFor(objectIdentity)).thenReturn(mutableAcl);
//
//        manager = new PermissionManager(aclManager, groupDao, aclUtil);
//    }
//
//    @Test(dataProvider = "accessChanges")
//    public void testChangeGrants(PermissionChanges changes) throws Exception {
//        Branch branch = ObjectsFactory.getDefaultBranch();
//
//        manager.changeGrants(branch, changes);
//
//        verify(aclManager, times(changes.getRemovedGroups().size())).
//                delete(anyListOf(Sid.class), eq(listFromArray(changes.getPermission())), eq(branch));
//
//        verify(aclManager, times(changes.getNewlyAddedGroupsAsArray().length)).
//                grant(anyListOf(Sid.class), eq(listFromArray(changes.getPermission())), eq(branch));
//    }
//
//    @Test(dataProvider = "accessChanges")
//    public void testChangeRestriction(PermissionChanges changes) throws Exception {
//        Branch branch = ObjectsFactory.getDefaultBranch();
//
//        manager.changeRestrictions(branch, changes);
//
//        verify(aclManager, times(changes.getRemovedGroups().size())).
//                delete(anyListOf(Sid.class), eq(listFromArray(changes.getPermission())), eq(branch));
//
//        verify(aclManager, times(changes.getNewlyAddedGroupsAsArray().length)).
//                restrict(anyListOf(Sid.class), eq(listFromArray(changes.getPermission())), eq(branch));
//    }
//
//    @Test
//    public void testGetPermissionsMapForBranch() throws Exception {
//        Branch branch = PersistedObjectsFactory.getDefaultBranch();
//        givenPermissions(branch, BranchPermission.values());
//
//        GroupsPermissions<BranchPermission> groupsPermissions = manager.getPermissionsMapFor(branch);
//        verify(aclManager).getGroupPermissionsOn(branch);
//        verify(aclUtil, times(BranchPermission.values().length)).getAclFor(branch);
//        assertTrue(groupsPermissions.getPermissions().containsAll(permissions));
//        for (GroupAce groupAce : groupAces) {
//            List<Group> groups = groupsPermissions.get((BranchPermission) groupAce.getPermission(), groupAce.isGranting());
//            assertNotNull(getGroupWithId(groups, groupAce.getGroupId()));
//            assertTrue(groups.contains(AnonymousGroup.ANONYMOUS_GROUP));
//        }
//    }
//
//    @Test
//    public void testChangeGrantsOfAnonymousGroup() throws Exception {
//        Branch branch = ObjectsFactory.getDefaultBranch();
//        PermissionChanges changes = new PermissionChanges(BranchPermission.CLOSE_TOPICS);
//        List<Group> groupList = new ArrayList<Group>();
//        groupList.add(AnonymousGroup.ANONYMOUS_GROUP);
//        changes.addNewlyAddedGroups(groupList);
//        manager.changeGrants(branch, changes);
//        List<Sid> sids = new ArrayList<Sid>();
//        sids.add(UserSid.createAnonymous());
//
//        verify(aclManager, times(changes.getRemovedGroups().size())).
//                delete(eq(sids), eq(listFromArray(changes.getPermission())), eq(branch));
//
//        verify(aclManager, times(changes.getNewlyAddedGroupsAsArray().length)).
//                grant(eq(sids), eq(listFromArray(changes.getPermission())), eq(branch));
//    }
//
//    @Test
//    public void testRestrictGrantsOfAnonymousGroup() throws Exception {
//        Branch branch = ObjectsFactory.getDefaultBranch();
//        PermissionChanges changes = new PermissionChanges(BranchPermission.CLOSE_TOPICS);
//        List<Group> groupList = new ArrayList<Group>();
//        groupList.add(AnonymousGroup.ANONYMOUS_GROUP);
//        changes.addNewlyAddedGroups(groupList);
//        manager.changeRestrictions(branch, changes);
//        List<Sid> sids = new ArrayList<Sid>();
//        sids.add(UserSid.createAnonymous());
//
//        verify(aclManager, times(changes.getRemovedGroups().size())).
//                delete(eq(sids), eq(listFromArray(changes.getPermission())), eq(branch));
//
//        verify(aclManager, times(changes.getNewlyAddedGroupsAsArray().length)).
//                restrict(eq(sids), eq(listFromArray(changes.getPermission())), eq(branch));
//    }
//
//    @Test
//    public void testDeleteGrantsOfAnonymousGroup() throws Exception {
//        Branch branch = ObjectsFactory.getDefaultBranch();
//        ;
//        PermissionChanges changes = new PermissionChanges(BranchPermission.CLOSE_TOPICS);
//        changes.addRemovedGroups(Lists.newArrayList(AnonymousGroup.ANONYMOUS_GROUP));
//        manager.changeGrants(branch, changes);
//        List<Sid> sids = Lists.<Sid>newArrayList(UserSid.createAnonymous());
//
//        verify(aclManager, times(changes.getRemovedGroups().size())).
//                delete(eq(sids), eq(listFromArray(changes.getPermission())), eq(branch));
//
//        verify(aclManager, times(changes.getNewlyAddedGroupsAsArray().length)).
//                grant(eq(sids), eq(listFromArray(changes.getPermission())), eq(branch));
//    }
//
//    @DataProvider
//    public Object[][] accessChanges() {
//        PermissionChanges accessChanges = new PermissionChanges(BranchPermission.CLOSE_TOPICS);
//        accessChanges.addNewlyAddedGroups(newArrayList(new Group("new1"), new Group("new2")));
//        accessChanges.addRemovedGroups(newArrayList(new Group("removed1"), new Group("removed2")));
//        return new Object[][]{{accessChanges}};
//    }
//
//    @DataProvider
//    public Object[][] branches() {
//        return new Object[][]{{ObjectsFactory.getDefaultBranch()}};
//    }
//
//    private List<Permission> listFromArray(Permission... permissions) {
//        return Lists.newArrayList(permissions);
//    }
//
//    private void givenPermissions(Entity entity, JtalksPermission... permissions) {
//        givenGroupAces(entity, permissions);
//
//        Answer<Group> answer = new GroupDaoAnswer(groups);
//
//        when(groupDao.get(anyLong())).thenAnswer(answer);
//        when(aclManager.getGroupPermissionsOn(eq(entity))).thenReturn(groupAces);
//    }
//
//    private void givenGroupAces(Entity entity, JtalksPermission... permissions) {
//        long entityId = entity.getId();
//
//        AuditLogger auditLogger = new ConsoleAuditLogger();
//        AclAuthorizationStrategy aclAuthorizationStrategy =
//                new org.springframework.security.acls.domain.AclAuthorizationStrategyImpl(
//                        new GrantedAuthorityImpl("some_role")
//                );
//        ObjectIdentity entityIdentity = new AclUtil(null).createIdentity(entityId,
//                entity.getClass().getSimpleName());
//        ExtendedMutableAcl mutableAcl = mock(ExtendedMutableAcl.class);
//        List<AccessControlEntry> accessControlEntries = new ArrayList<AccessControlEntry>();
//
//        Acl acl = new AclImpl(entityIdentity, entityId + 1, aclAuthorizationStrategy, auditLogger);
//
//        long lastGroupId = 1;
//
//        for (int i = 0; i < permissions.length; i++) {
//            for (int j = 0, count = RandomUtils.nextInt(20) + 10; j < count; j++) {
//                Group group = randomGroup(lastGroupId++);
//                groups.add(group);
//
//                this.permissions.add(permissions[i]);
//                groupAces.add(buildGroupAce(entity, permissions[i], (i % 2 == 1), acl,
//                        new UserGroupSid(group.getId())));
//            }
//            AccessControlEntry controlEntry = mock(AccessControlEntry.class);
//            when(controlEntry.getPermission()).thenReturn(permissions[i]);
//            when(controlEntry.getSid()).thenReturn(UserSid.createAnonymous());
//            when(controlEntry.isGranting()).thenReturn((i % 2 == 1));
//            accessControlEntries.add(controlEntry);
//        }
//        when(mutableAcl.getEntries()).thenReturn(accessControlEntries);
//        when(aclUtil.getAclFor(entity)).thenReturn(mutableAcl);
//    }
//
//    private GroupAce buildGroupAce(Entity entity, JtalksPermission permission, boolean isGranting, Acl acl, Sid sid) {
//        AccessControlEntry accessControlEntry = new AccessControlEntryImpl(entity.getId(), acl, sid, permission,
//                isGranting, false, false);
//        return new GroupAce(accessControlEntry);
//    }
}
