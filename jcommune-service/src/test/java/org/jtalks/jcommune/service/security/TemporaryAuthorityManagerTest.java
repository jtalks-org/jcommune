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

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Anuar Nurmakanov
 */
public class TemporaryAuthorityManagerTest {
    @Mock
    private SecurityContextFacade securityContextFacade;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private TemporaryAuthorityManager.SecurityOperation operation;
    private TemporaryAuthorityManager temporaryAuthorityManager;

    @BeforeMethod
    public void init() {
        MockitoAnnotations.initMocks(this);
        temporaryAuthorityManager = new TemporaryAuthorityManager(securityContextFacade);

        Mockito.when(securityContextFacade.getContext()).thenReturn(securityContext);

    }

    @Test
    public void testRunWithTemporaryAuthority() {
        Authentication authentication = Mockito.mock(Authentication.class);
        Collection<GrantedAuthority> authorities = Collections.emptyList();

        Mockito.when(authentication.getAuthorities()).thenReturn(authorities);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

        temporaryAuthorityManager.runWithTemporaryAuthority(operation, SecurityConstants.ROLE_ADMIN);

        Mockito.verify(operation).doOperation();
        Mockito.verify(securityContext, Mockito.times(2)).setAuthentication(
                Mockito.any(Authentication.class));
    }
}
