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
package org.jtalks.jcommune.web.tags;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PaginatorTest {
    private static final int USER_PAGE_SIZE = 5;
    private Paginator paginator;
    private MockPageContext pageContext;
    private List<Object> list;


    @BeforeMethod
    protected void setUp() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        WebApplicationContext context = mock(WebApplicationContext.class);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        pageContext = new MockPageContext(servletContext);
        paginator = new Paginator();
        paginator.setPageContext(pageContext);

        when(context.getServletContext()).thenReturn(servletContext);

        list = Arrays.asList((Object)1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testCreatePagingLinkPagingEnabled() {
        Pageable pageable = new PageRequest(1, USER_PAGE_SIZE);
        Page<Object> page = new PageImpl<Object>(list, pageable, 10);
        paginator.setPage(page);
        paginator.setPagingEnabled(true);

        assertEquals(paginator.createPagingLink(5, "1"),
                "<li class='active'><a href='#'>1</a></li><li><a href='1?page=2'>2</a></li>");
    }

   @Test
    public void testCreatePagingLinkPagingDisabled() {
        Pageable pageable = new PageRequest(1, USER_PAGE_SIZE);
        Page<Object> page = new PageImpl<Object>(list, pageable, 10);
        paginator.setPage(page);
        paginator.setPagingEnabled(false);

        assertEquals(paginator.createPagingLink(5, "1"), StringUtils.EMPTY);
    }

    @Test
    public void testCreatePagingLink() {
        Pageable pageable = new PageRequest(2, USER_PAGE_SIZE);
        Page<Object> page = new PageImpl<Object>(list, pageable, 15);
        paginator.setPage(page);
        paginator.setPagingEnabled(true);

        assertEquals(paginator.createPagingLink(5, "1"),
                "<li><a href='1?page=1'>1</a></li><li class='active'><a href='#'>2</a></li><li><a href='1?page=3'>3</a></li>");
    }
    
    @Test
    public void testCreatePagingLinkWithSinglePage() {
        Pageable pageable = new PageRequest(1, USER_PAGE_SIZE);
        Page<Object> page = new PageImpl<Object>(list, pageable, 5);
        paginator.setPage(page);
        paginator.setPagingEnabled(true);
        
        String pagingLink = paginator.createPagingLink(5, "1"); 
        assertEquals(pagingLink, StringUtils.EMPTY);
    }
}
