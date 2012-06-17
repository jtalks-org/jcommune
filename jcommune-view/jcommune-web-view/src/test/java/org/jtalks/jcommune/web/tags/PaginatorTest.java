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

import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.web.util.Pagination;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.jsp.JspException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class PaginatorTest {

    private Paginator paginator;
    private MockPageContext pageContext;
    private JCUser user;
    private List list;


    @BeforeMethod
    protected void setUp() throws Exception {
        MockServletContext servletContext = new MockServletContext();
        WebApplicationContext context = mock(WebApplicationContext.class);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        pageContext = new MockPageContext(servletContext);
        paginator = new Paginator();
        paginator.setPageContext(pageContext);

        when(context.getServletContext()).thenReturn(servletContext);

        user = new JCUser("", "", "");
        user.setPageSize(5);
        list = Arrays.asList(1, 2, 3, 4, 5, 6);
    }

    /* @Test
    public void testElementsOfPage() throws JspException {
        JCUser user = new JCUser("", "", "");
        user.setPageSize(5);
        Pagination pagination = new Pagination(1, user, 6, true);
        paginator.setPagination(pagination);

        paginator.setList(list);

        paginator.doStartTag();

        assertEquals(pageContext.getAttribute("list"), list.subList(0, 5));

        paginator.doEndTag();

        assertEquals(pagination.getMaxPages(), 2);
    }

    @Test
    public void testLastPage() {
        Pagination pagination = new Pagination(2, user, 6, true);
        paginator.setPagination(pagination);
        paginator.setList(list);

        paginator.doStartTag();

        assertEquals(pageContext.getAttribute("list"), Collections.singletonList(6));
    }

    @Test
    public void testUserAnonymous() {
        Pagination pagination = new Pagination(2, null, 55, true);
        paginator.setPagination(pagination);
        List list = Collections.nCopies(55, 1);

        paginator.setList(list);

        paginator.doStartTag();

        assertEquals(pageContext.getAttribute("list"), list.subList(50, 55));
    }

    @Test
    public void testSizeOne() {
        Pagination pagination = new Pagination(1, user, 6, false);
        paginator.setPagination(pagination);

        paginator.setList(list);

        paginator.doStartTag();

        assertEquals(pageContext.getAttribute("list"), list);
    }

    @Test
    public void testEmptyList() throws JspException {
        Pagination pagination = new Pagination(1, user, 6, true);
        paginator.setPagination(pagination);
        List list = new ArrayList();

        paginator.setList(list);

        paginator.doStartTag();

        paginator.doEndTag();

        assertEquals(pageContext.getAttribute("list"), null);
    }

    @Test
    public void testDisablePaging() {
        Pagination pagination = new Pagination(1, null, 55, false);
        paginator.setPagination(pagination);
        List list = Collections.nCopies(55, 1);

        paginator.setList(list);

        paginator.doStartTag();

        assertEquals(pageContext.getAttribute("list"), list);
    }

    @Test
    public void testCreatePagingLinkPagingEnabled() {
        Pagination pagination = new Pagination(1, user, 10, true);
        paginator.setPagination(pagination);

        assertEquals(paginator.createPagingLink(5, "1"),
                "<li class='active'><a href='#'>1</a></li><li><a href='1?page=2'>2</a></li>");
    }

    @Test
    public void testCreatePagingLinkPagingDisabled() {
        Pagination pagination = new Pagination(1, user, 10, false);
        paginator.setPagination(pagination);

        assertEquals(paginator.createPagingLink(5, "1"), "");
    }

    @Test
    public void testCreatePagingLink() {
        Pagination pagination = new Pagination(2, user, 15, true);
        paginator.setPagination(pagination);

        assertEquals(paginator.createPagingLink(5, "1"),
                "<li><a href='1?page=1'>1</a></li><li class='active'><a href='#'>2</a></li><li><a href='1?page=3'>3</a></li>");
    }
    
    @Test
    public void testCreatePagingLinkWithSinglePage() {
    	Pagination pagination = new Pagination(1, user, 5, true);
        paginator.setPagination(pagination);
        String pagingLink = paginator.createPagingLink(5, "1"); 
        assertEquals(pagingLink, "");
    }*/
}
