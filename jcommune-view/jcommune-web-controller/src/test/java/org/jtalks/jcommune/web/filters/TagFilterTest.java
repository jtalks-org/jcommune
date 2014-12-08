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
package org.jtalks.jcommune.web.filters;

import org.jtalks.jcommune.web.filters.parsers.TagParser;
import org.jtalks.jcommune.web.filters.wrapper.TaggedResponseWrapper;
import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Mikhail Stryzhonok
 */
public class TagFilterTest {

    @Mock
    private TagParser parser1, parser2;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeClass
    public void init() {
        initMocks(this);
    }

    @BeforeMethod
    public void refresh() throws Exception{
        filterChain = new MockFilterChain();
        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        when(parser1.replaceTagByContent(any(TaggedResponseWrapper.class))).thenReturn(new byte[0]);
        when(parser2.replaceTagByContent(any(TaggedResponseWrapper.class))).thenReturn(new byte[0]);
    }

    @Test
    public void doFilterShouldCallAllParsersIfContentTypeIsTextHtml() throws Exception {
        TagFilter filter = new TagFilter();
        List<TagParser> parsers = new ArrayList<>();
        parsers.add(parser1);
        parsers.add(parser2);
        filter.setParsers(parsers);
        response.setContentType("text/html");

        filter.doFilter(request, response, filterChain);

        for (TagParser parser : parsers) {
            verify(parser).replaceTagByContent(any(TaggedResponseWrapper.class));
        }
    }

    @Test
    public void doFilterShouldNotCallParsersIfContentTypeNotTextHtml() throws Exception{
        TagFilter filter = new TagFilter();
        List<TagParser> parsers = new ArrayList<>();
        parsers.add(parser1);
        parsers.add(parser2);
        filter.setParsers(parsers);

        filter.doFilter(request, response, filterChain);

        for (TagParser parser : parsers) {
            verify(parser).replaceTagByContent(any(TaggedResponseWrapper.class));
        }
    }
}
