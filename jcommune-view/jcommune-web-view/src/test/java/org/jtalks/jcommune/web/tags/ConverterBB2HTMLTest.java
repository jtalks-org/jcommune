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

import org.jtalks.jcommune.service.nontransactional.BBCodeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class ConverterBB2HTMLTest {

    private ConverterBB2HTML tag;
    private BBCodeService bbCodeService;
    private MockPageContext pageContext;

    @Before
    public void setUp() {
        tag = new ConverterBB2HTML();

        bbCodeService = mock(BBCodeService.class);

        ServletContext servletContext = new MockServletContext();
        GenericWebApplicationContext wac = (GenericWebApplicationContext) BeanUtils
                .instantiateClass(GenericWebApplicationContext.class);
        wac.getBeanFactory().registerSingleton("BBCodeService", bbCodeService);
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
        pageContext = new MockPageContext(servletContext);
    }

    @Test
    public void testConverterTag() throws JspException, UnsupportedEncodingException {
        String expected = "result";
        String source = "source";
        when(bbCodeService.convertBbToHtml(source)).thenReturn(expected);

        tag.setPageContext(pageContext);
        tag.setBbCode(source);

        tag.doStartTag();

        String output = ((MockHttpServletResponse) pageContext.getResponse()).getContentAsString();
        assertEquals(output, expected);
    }



}
