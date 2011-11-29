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

import org.jtalks.jcommune.model.entity.User;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class PaginationTest {
    private Pagination pagination;
    private String link;
    private String uri;
    private User user;
    private List list;

    @BeforeMethod
    protected void setUp() {
        user = mock(User.class);
        when(user.getPageSize()).thenReturn("FIVE");
        uri = "1";
        link = "<a href=\"%s?page=%d\">%d</a>";
    }

    @Test
    public void testCreatePagingLink() {
        pagination = new Pagination(1, user, 10, true);

        String comletedLinks = pagination.createPagingLink(5, link, uri);

        assertEquals(comletedLinks, "1      <a href=\"1?page=2\">2</a>");

        pagination = new Pagination(1, user, 10, false);

        comletedLinks = pagination.createPagingLink(5, link, uri);

        assertEquals(comletedLinks, "");

        pagination = new Pagination(2, user, 15, true);

        comletedLinks = pagination.createPagingLink(5, link, uri);

        assertEquals(comletedLinks, "<a href=\"1?page=1\">1</a>2      <a href=\"1?page=3\">3</a>");
    }

    @Test
    public void testNumberOfPages() {
        pagination = new Pagination(1, user, 10, true);

        list = Collections.nCopies(10, 1);

        List lists = pagination.integerNumberOfPages(list);

        assertEquals(lists, list.subList(0, 5));

        pagination = new Pagination(2, user, 10, true);

        list = Collections.nCopies(7, 1);

        lists = pagination.notIntegerNumberOfPages(list);

        assertEquals(lists, list.subList(5, 7));

    }

    @Test
    public void testConstructor() {
        pagination = new Pagination(1, user, 10);

        assertEquals((int) pagination.getPage(), 1);
        assertEquals(pagination.getPageSize(), 5);

        pagination = new Pagination(1, null, 10);

        assertEquals((int) pagination.getPageSize(), 50);
    }

    @Test
    public void testMaxPages()
    {
        pagination = new Pagination(1, user, 10);

        assertEquals(pagination.getMaxPages(),2);
    }
}
