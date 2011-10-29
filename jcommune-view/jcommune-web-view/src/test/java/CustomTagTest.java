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

import org.jtalks.jcommune.web.tags.Paginator;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class CustomTagTest {

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
    public void testCustomTag() {
        List list = new ArrayList();
        list.add(1);
        list.add(2);
        list.add(3);

        paginator.setList(list);
        paginator.setCurrentPage(1);
        paginator.setNumberElement(2);

        paginator.doStartTag();

        assertEquals(PageContext.getAttribute("list"), list.subList(0, 2));

        paginator.doEndTag();

        assertEquals(PageContext.getAttribute("maxPage"), 2);
    }
}
