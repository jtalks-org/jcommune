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

import org.jtalks.common.model.permissions.BranchPermission;
import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.service.transactional.TransactionalPermissionService;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class TransactionalPermissionServiceTest {

    @Mock
    private SecurityContextHolderFacade contextFacade;
    @Mock
    private AclGroupPermissionEvaluator aclEvaluator;

    private PermissionService permissionService;

    @BeforeMethod
    public void initEnvironmental() {
        initMocks(this);

        permissionService = new TransactionalPermissionService(contextFacade, aclEvaluator);
    }

    @BeforeMethod
    public void prepareTestData() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(contextFacade.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    public void testHasPermissionStringPermissionPermissionGranted() {
        doReturn(true).when(aclEvaluator)
                .hasPermission(any(Authentication.class), any(Serializable.class), anyString(), anyString());

        assertTrue(permissionService.hasPermission(0, null, ""));
    }

    @Test
    public void testHasPermissionStringPermissionPermissionNotGranted() {
        doReturn(false).when(aclEvaluator)
                .hasPermission(any(Authentication.class), any(Serializable.class), anyString(), anyString());

        assertFalse(permissionService.hasPermission(0, null, ""));
    }

    @Test
    public void testHasPermissionEnumPermissionPermissionGranted() {
        doReturn(true).when(aclEvaluator)
                .hasPermission(any(Authentication.class), any(Serializable.class), anyString(), anyString());

        assertTrue(permissionService.hasPermission(0, AclClassName.BRANCH, GeneralPermission.READ));
    }

    @Test
    public void testHasPermissionEnumPermissionPermissionNotGranted() {
        doReturn(false).when(aclEvaluator)
                .hasPermission(any(Authentication.class), any(Serializable.class), anyString(), anyString());

        assertFalse(permissionService.hasPermission(0, AclClassName.BRANCH, GeneralPermission.READ));
    }

    @Test
    public void testCheckPermissionPermissionGranted() {
        doReturn(true).when(aclEvaluator)
                .hasPermission(any(Authentication.class), any(Serializable.class), anyString(), anyString());

        permissionService.checkPermission(0, AclClassName.BRANCH, GeneralPermission.READ);
    }

    @Test(expectedExceptions = AccessDeniedException.class)
    public void testCheckPermissionPermissionNotGranted() {
        doReturn(false).when(aclEvaluator)
                .hasPermission(any(Authentication.class), any(Serializable.class), anyString(), anyString());

        permissionService.checkPermission(0, AclClassName.BRANCH, GeneralPermission.READ);
    }

    /**
     * Tests whether method constructs data that is going to be passed to the evaluator is correct and thus {@link
     * PermissionService#hasBranchPermission(long, BranchPermission)} will return same result as evaluator.
     */
    @Test
    public void hasBranchPermissionIsTrueIfEvaluatorSaidSo() {
        doReturn(true).when(aclEvaluator).hasPermission(any(Authentication.class),
                eq(1L),
                eq(AclClassName.BRANCH.toString()),
                eq("BranchPermission.EDIT_OWN_POSTS"));

        permissionService.hasBranchPermission(1L, BranchPermission.EDIT_OWN_POSTS);
    }
}