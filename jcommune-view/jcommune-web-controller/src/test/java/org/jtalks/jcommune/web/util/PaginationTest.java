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

import org.jtalks.common.model.entity.Entity;
import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.nontransactional.LocationService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

public class PaginationTest {
    private Pagination pagination;

    private User user;
    private LocationService locationServiceImpl;
    private ForumStatisticsProvider forumStatisticsProvider;
    private Entity entity;

    private static final int PAGE_SIZE = 5;

    @BeforeMethod
    protected void setUp() {
        entity = mock(Entity.class);
        locationServiceImpl = mock(LocationService.class);
        forumStatisticsProvider = mock(ForumStatisticsProvider.class);
        user = new User("", "", "");
        user.setPageSize(PAGE_SIZE);

    }

    @Test
    public void testNumberOfPages() {
        pagination = new Pagination(1, user, 10, true);

        List list = Collections.nCopies(10, 1);

        List lists = pagination.integerNumberOfPages(list);

        assertEquals(lists, list.subList(0, 5));

        pagination = new Pagination(2, user, 10, true);

        list = Collections.nCopies(7, 1);

        lists = pagination.notIntegerNumberOfPages(list);

        assertEquals(lists, list.subList(5, 7));

    }

    @Test
    public void testConstructor() {
        pagination = new Pagination(1, user, 10, true);

        assertEquals((int) pagination.getPage(), 1);
        assertEquals(pagination.getPageSize(), 5);

        pagination = new Pagination(1, null, 10, true);

        assertEquals(pagination.getPageSize(), 50);
    }

    @Test
    public void testMaxPages() {
        pagination = new Pagination(1, user, 10, true);

        assertEquals(pagination.getMaxPages(), 2);
        assertEquals(pagination.isLastPage(), false);
    }

    @Test
    public void testMaxPagesNotEqual() {
        pagination = new Pagination(1, user, 4, true);

        assertEquals(pagination.getMaxPages(), 1);
        assertEquals(pagination.isLastPage(), true);
    }

    @Test
    public void testReturnUserPageSize() {
        int pageSize = Pagination.getPageSizeFor(user);
        assertEquals(pageSize, PAGE_SIZE);
    }

    @Test
    public void testReturnDefaultPageSizeForNullUser() {
        int pageSize = Pagination.getPageSizeFor(null);
        assertEquals(pageSize, User.DEFAULT_PAGE_SIZE);
    }
}
