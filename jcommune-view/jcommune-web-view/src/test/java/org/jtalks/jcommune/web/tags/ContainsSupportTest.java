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

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ContainsSupportTest {

    private String successMessage = "contains";
    private String failMessage = "not contains";
    private Object object;
    private Collection collection;

    private PageContext context;

    private ContainsSupport tag;

    @Before
    public void init() {
        object = new Object();
        tag = new ContainsSupport();
        context = new MockPageContext(new MockServletContext(), new MockHttpServletRequest());
    }

    @Test
    public void testContains() throws JspException, UnsupportedEncodingException {
        collection = Collections.singleton(object);

        assertEquals(this.render(), successMessage);
    }

    @Test
    public void testNotContains() throws JspException, UnsupportedEncodingException {
        collection = Collections.singleton(new Object());

        assertEquals(this.render(), failMessage);
    }

    private String render() throws JspException, UnsupportedEncodingException {
        //cannot move it to @Before as test data should be set first
        tag.setPageContext(context);
        tag.setCollection(collection);
        tag.setObject(object);
        tag.setSuccessMessage(successMessage);
        tag.setFailMessage(failMessage);
        tag.doStartTag();
        return ((MockHttpServletResponse) context.getResponse()).getContentAsString();
    }
}
