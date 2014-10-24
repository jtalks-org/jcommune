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

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Needed to filter response and replace custom tags by appropriate content
 *
 * @author Mikhail Stryzhonok
 */
public class TagFilter implements Filter {
    private List<TagParser> parsers = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        TaggedResponseWrapper wrappedResponse = new TaggedResponseWrapper((HttpServletResponse)response);
        chain.doFilter(request, wrappedResponse);
        String encoding = wrappedResponse.getCharacterEncoding();
        byte[] bytes = wrappedResponse.getByteArray();
        if (wrappedResponse.getContentType() != null && wrappedResponse.getContentType().contains("text/html")) {
            for (TagParser parser : parsers) {
                bytes = parser.replaceTagByContent(wrappedResponse);
            }
            response.setContentLength(bytes.length);
        }
        response.getOutputStream().write(bytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {

    }

    /**
     * Sets list of parsers to be applied to response
     * @param parsers list of parsers
     */
    public void setParsers(List<TagParser> parsers) {
        this.parsers = parsers;
    }
}
