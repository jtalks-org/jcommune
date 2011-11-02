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

import org.jtalks.jcommune.model.entity.User;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class PaginatorTest {

    private Paginator paginator;
    private MockServletContext ServletContext;
    private MockPageContext PageContext;
    private WebApplicationContext WebApplicationContext;

    @BeforeMethod
    protected void setUp() throws Exception {
        ServletContext = new MockServletContext();
        WebApplicationContext = mock(WebApplicationContext.class);
        ServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                WebApplicationContext);
        PageContext = new MockPageContext(ServletContext);
        paginator = new Paginator();
        paginator.setPageContext(PageContext);

        when(WebApplicationContext.getServletContext()).thenReturn(ServletContext);
    }

    @Test
    public void testElementsOfPage() {
        SecurityService securityService = mock(SecurityService.class);
        User user = new User("", "", "");
        user.setPageSize("FIVE");
        List list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        paginator.setList(list);
        paginator.setCurrentPage(1);
        when(WebApplicationContext.getBean("securityService")).thenReturn(securityService);
        when(securityService.getCurrentUser()).thenReturn(user);

        paginator.doStartTag();

        assertEquals(PageContext.getAttribute("list"), list.subList(0, 5));

        paginator.doEndTag();

        assertEquals(PageContext.getAttribute("maxPage"), 2);
    }

    @Test
    public void testGetMaxPage() {
        int itemCount = 5;
        int numberElement = 5;

        assertEquals(paginator.getMaxPage(itemCount, numberElement), 1);
    }

    @Test
    public void testLastPage() {
        SecurityService securityService = mock(SecurityService.class);
        User user = new User("", "", "");
        user.setPageSize("FIVE");
        when(WebApplicationContext.getBean("securityService")).thenReturn(securityService);
        when(securityService.getCurrentUser()).thenReturn(user);
        List list1 = new ArrayList();
        list1.add(6);
        List list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        paginator.setList(list);
        paginator.setCurrentPage(2);
        paginator.setMaxPages(2);

        paginator.doStartTag();

        assertEquals(PageContext.getAttribute("list"), list1);
    }


    @Test
    public void testDoEndTag() {
        paginator.setCurrentPage(2);
        paginator.setMaxPages(3);

        assertEquals(paginator.doEndTag(), 6);
    }
}
