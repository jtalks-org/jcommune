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
package org.jtalks.jcommune.web.util;

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.plugin.api.exceptions.NotFoundException;
import org.jtalks.jcommune.service.nontransactional.ForumStatisticsService;
import org.jtalks.jcommune.web.listeners.SessionStatisticListener;
import org.springframework.security.core.session.SessionRegistry;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test for {@link ForumStatisticsProvider}.
 *
 * @author Elena Lepaeva
 */
public class ForumStatisticsProviderTest {

    private ForumStatisticsService statisticsService;
    private ForumStatisticsProvider forumStaticsProvider;

    private int userCount = 5;
    private long sessionCount = 7;
    private List<Object> users;

    @BeforeClass
    public void setUp() {

        statisticsService = mock(ForumStatisticsService.class);

        SessionRegistry sessionRegistry = mock(SessionRegistry.class);
        users = Collections.nCopies(userCount , (Object) new JCUser("","",""));
        when(sessionRegistry.getAllPrincipals()).thenReturn(users);

        SessionStatisticListener listener = mock(SessionStatisticListener.class);
        when(listener.getTotalActiveSessions()).thenReturn(sessionCount);

        forumStaticsProvider = new ForumStatisticsProvider(sessionRegistry, listener, statisticsService);
    }

    @Test
    public void testGetPostsOnForumCount() throws NotFoundException {
        int expectedCount = 10;
        when(statisticsService.getPostsOnForumCount()).thenReturn(expectedCount);

        assertEquals(forumStaticsProvider.getPostsOnForumCount(), expectedCount);
        verify(statisticsService).getPostsOnForumCount();
    }

    @Test
    public void testGetUsersCount() throws Exception {
        when(statisticsService.getUsersCount()).thenReturn(userCount);

        assertEquals(forumStaticsProvider.getUsersCount(), userCount);
        verify(statisticsService).getUsersCount();
    }

    @Test
    public void getOnlineRegisteredUsersTest() throws Exception {
        assertEquals(forumStaticsProvider.getOnlineRegisteredUsers(), users);
    }

    @Test
    public void getOnlineUsersCountTest() throws Exception {
        assertEquals(forumStaticsProvider.getOnlineUsersCount(), sessionCount);
    }

    @Test
    public void getOnlineRegisteredUsersCountTest() throws Exception {
        assertEquals(forumStaticsProvider.getOnlineRegisteredUsersCount(), userCount);
    }

    @Test
    public void getOnlineAnonymousUsersCountTest() throws Exception {
        assertEquals(forumStaticsProvider.getOnlineAnonymousUsersCount(), sessionCount - userCount);
    }
}
