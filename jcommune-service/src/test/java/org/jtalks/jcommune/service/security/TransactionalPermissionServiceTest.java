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

import java.io.Serializable;

import org.jtalks.common.model.permissions.GeneralPermission;
import org.jtalks.common.service.security.SecurityContextHolderFacade;
import org.jtalks.jcommune.service.security.PermissionService;
import org.mockito.Mock;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

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
        when(aclEvaluator.hasPermission(
                any(Authentication.class), 
                any(Serializable.class), 
                anyString(), 
                anyString()))
        .thenReturn(true);
        
        assertTrue(permissionService.hasPermission(0, null, ""));
    }
    
    @Test
    public void testHasPermissionStringPermissionPermissionNotGranted() {
        when(aclEvaluator.hasPermission(
                any(Authentication.class), 
                any(Serializable.class), 
                anyString(), 
                anyString()))
        .thenReturn(false);
        
        assertFalse(permissionService.hasPermission(0, null, ""));
    }
    
    @Test
    public void testHasPermissionEnumPermissionPermissionGranted() {
        when(aclEvaluator.hasPermission(
                any(Authentication.class), 
                any(Serializable.class), 
                anyString(), 
                anyString()))
        .thenReturn(true);
        
        assertTrue(permissionService.hasPermission(0, AclClassName.BRANCH, GeneralPermission.READ));
    }
    
    @Test
    public void testHasPermissionEnumPermissionPermissionNotGranted() {
        when(aclEvaluator.hasPermission(
                any(Authentication.class), 
                any(Serializable.class), 
                anyString(), 
                anyString()))
        .thenReturn(false);
        
        assertFalse(permissionService.hasPermission(0, AclClassName.BRANCH, GeneralPermission.READ));
    }
    
    @Test
    public void testCheckPermissionPermissionGranted() {
        when(aclEvaluator.hasPermission(
                any(Authentication.class), 
                any(Serializable.class), 
                anyString(), 
                anyString()))
        .thenReturn(true);
        
        permissionService.checkPermission(0, AclClassName.BRANCH, GeneralPermission.READ);
    }
    
    @Test(expectedExceptions=AccessDeniedException.class)
    public void testCheckPermissionPermissionNotGranted() {
        when(aclEvaluator.hasPermission(
                any(Authentication.class), 
                any(Serializable.class), 
                anyString(), 
                anyString()))
        .thenReturn(false);
        
        permissionService.checkPermission(0, AclClassName.BRANCH, GeneralPermission.READ);
    }
}