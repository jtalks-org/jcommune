/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.service.security;

import org.jtalks.jcommune.model.entity.Entity;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

/**
 * @author Kirill Afonin
 */
public class AclManagerImplTest {
    public static final long ID = 2L;
    public static final String ROLE = "ROLE_USER";
    public static final String USERNAME = "username";
    private List<Sid> sids = new ArrayList<Sid>();
    private List<Permission> permissions = new ArrayList<Permission>();
    private Entity target = new Entity() {
        @Override
        public long getId() {
            return ID;
        }
    };
    private AclManager manager;
    private MutableAclService aclService;

    @BeforeMethod
    public void setUp() throws Exception {
        aclService = mock(MutableAclService.class);
        manager = new AclManagerImpl(aclService);
        sids.add(new GrantedAuthoritySid(ROLE));
        sids.add(new PrincipalSid(USERNAME));
        permissions.add(BasePermission.READ);
    }

    @Test
    public void testGrantOnObjectWithNotExistingAcl() throws Exception {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(target.getClass(), ID);
        MutableAcl objectAcl = new AclImpl(objectIdentity, 2L, mock(AclAuthorizationStrategy.class),
                mock(AuditLogger.class));
        when(aclService.readAclById(objectIdentity)).thenThrow(new NotFoundException(""));
        when(aclService.createAcl(objectIdentity)).thenReturn(objectAcl);

        manager.grant(sids, permissions, target);

        assertGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.READ, "Permission to user not granted");
        assertGranted(objectAcl, new GrantedAuthoritySid(ROLE),
                BasePermission.READ, "Permission to ROLE_USER not granted");
        verify(aclService).readAclById(objectIdentity);
        verify(aclService).createAcl(objectIdentity);
        verify(aclService).updateAcl(objectAcl);
    }

    @Test
    public void testGrantOnObjectWithExistingAcl() throws Exception {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(target.getClass(), ID);
        MutableAcl objectAcl = new AclImpl(objectIdentity, 2L, mock(AclAuthorizationStrategy.class),
                mock(AuditLogger.class));
        when(aclService.readAclById(objectIdentity)).thenReturn(objectAcl);

        manager.grant(sids, permissions, target);

        assertGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.READ, "Permission to user not granted");
        assertGranted(objectAcl, new GrantedAuthoritySid(ROLE),
                BasePermission.READ, "Permission to ROLE_USER not granted");
        verify(aclService).readAclById(objectIdentity);
        verify(aclService).updateAcl(objectAcl);
    }

    @Test
    public void testDelete() throws Exception {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(target.getClass(), ID);
        MutableAcl objectAcl = new AclImpl(objectIdentity, 2L, mock(AclAuthorizationStrategy.class),
                mock(AuditLogger.class));
        objectAcl.insertAce(objectAcl.getEntries().size(), BasePermission.READ,
                new PrincipalSid(USERNAME), true);
        objectAcl.insertAce(objectAcl.getEntries().size(), BasePermission.READ,
                new GrantedAuthoritySid(ROLE), true);
        objectAcl.insertAce(objectAcl.getEntries().size(), BasePermission.DELETE,
                new GrantedAuthoritySid(ROLE), true);
        when(aclService.readAclById(objectIdentity)).thenReturn(objectAcl);


        manager.delete(sids, permissions, target);

        assertNotGranted(objectAcl, new PrincipalSid(USERNAME),
                BasePermission.READ, "Permission to user granted");
        assertNotGranted(objectAcl, new GrantedAuthoritySid(ROLE),
                BasePermission.READ, "Permission to ROLE_USER granted");
        assertGranted(objectAcl, new GrantedAuthoritySid(ROLE),
                BasePermission.DELETE, "Permission to ROLE_USER not granted");
        verify(aclService).readAclById(objectIdentity);
        verify(aclService).updateAcl(objectAcl);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testGrantWithZeroId() throws Exception {
        Entity object = new Entity() {
            @Override
            public long getId() {
                return 0;
            }
        };

        manager.grant(sids, permissions, object);
    }

    @Test
    public void testDeleteFromAcl() throws Exception {
        ObjectIdentity objectIdentity = new ObjectIdentityImpl(target.getClass(), target.getId());

        manager.deleteFromAcl(target.getClass(), ID);

        verify(aclService).deleteAcl(objectIdentity, true);
    }

    @Test(expectedExceptions = {IllegalStateException.class})
    public void testDeleteFromAclWithZeroId() throws Exception {
        manager.deleteFromAcl(target.getClass(), 0);
    }

    private void assertNotGranted(MutableAcl acl, Sid sid, Permission permission, String message) {
        List<Permission> expectedPermission = new ArrayList<Permission>();
        expectedPermission.add(permission);
        List<Sid> expectedSid = new ArrayList<Sid>();
        expectedSid.add(sid);
        try {
            acl.isGranted(expectedPermission, expectedSid, true);
            fail(message);
        } catch (NotFoundException e) {
        }
    }


    private void assertGranted(MutableAcl acl, Sid sid, Permission permission, String message) {
        List<Permission> expectedPermission = new ArrayList<Permission>();
        expectedPermission.add(permission);
        List<Sid> expectedSid = new ArrayList<Sid>();
        expectedSid.add(sid);
        try {
            assertTrue(acl.isGranted(expectedPermission, expectedSid, true), message);
        } catch (NotFoundException e) {
            fail(message);
        }
    }
}
