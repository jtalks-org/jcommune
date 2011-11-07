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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.dao.ForumStatisticsDAO;
import org.jtalks.jcommune.service.ForumStatisticsService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Test for {@link ForumStatisticsServiceImpl}.
 *
 * @author Elena Lepaeva
 */
public class ForumStatisticsServiceTest {

    private ForumStatisticsService statisticsService;
    private ForumStatisticsDAO statisticsDAO;

    @BeforeClass
    public void setUp() {
        statisticsDAO = mock(ForumStatisticsDAO.class);
        statisticsService = new ForumStatisticsServiceImpl(statisticsDAO);
    }

    @Test
    public void testGetPostsOnForumCount() throws NotFoundException {
        int expectedCount = 10;
        when(statisticsDAO.getPostsOnForumCount()).thenReturn(expectedCount);

        assertEquals(statisticsService.getPostsOnForumCount(), expectedCount);
        verify(statisticsDAO).getPostsOnForumCount();
    }

    @Test
    public void testGetUsersCount() throws Exception {
        int userCount = 5;
        when(statisticsDAO.getUsersCount()).thenReturn(userCount);

        assertEquals(statisticsService.getUsersCount(), userCount);
        verify(statisticsDAO).getUsersCount();
    }
}
