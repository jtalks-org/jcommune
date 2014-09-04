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
package org.jtalks.jcommune.plugin.api.service;

import org.jtalks.jcommune.model.entity.AnonymousUser;
import org.jtalks.jcommune.model.entity.JCUser;
import org.junit.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.hamcrest.CoreMatchers.*;

import org.mockito.Mock;
import static org.unitils.reflectionassert.ReflectionAssert.*;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.*;

/**
 * @author Mikhail Stryzhonok
 */
public class ReadOnlySecurityServiceTest {
    @Mock
    private UserReader userReader;

    @BeforeMethod
    public void init() {
        initMocks(this);
    }

    @Test
    public void getCurrentUserShouldReturnCopyOfCurrentUser() {
        JCUser currentUser = new JCUser("name", "email@example.com", "password");
        when(userReader.getCurrentUser()).thenReturn(currentUser);
        ReadOnlySecurityService service = (ReadOnlySecurityService)ReadOnlySecurityService.getInstance();
        service.setUserReader(userReader);

        JCUser result = service.getCurrentUser();

        assertNotSame(result, currentUser);
        assertReflectionEquals(currentUser, result);
        Assert.assertThat(result, not(instanceOf(AnonymousUser.class)));
    }

    @Test
    public void getCurrentUserShouldReturnAnonymousUserIfCurrentUserNotFound() {
        when(userReader.getCurrentUser()).thenReturn(new AnonymousUser());
        ReadOnlySecurityService service = (ReadOnlySecurityService)ReadOnlySecurityService.getInstance();
        service.setUserReader(userReader);

        JCUser result = service.getCurrentUser();

        Assert.assertThat(result, instanceOf(AnonymousUser.class));
    }
}
