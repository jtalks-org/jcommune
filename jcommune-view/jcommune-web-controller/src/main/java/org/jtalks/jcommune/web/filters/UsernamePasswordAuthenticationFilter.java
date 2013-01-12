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

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This filter performs the same operations as {@link UsernamePasswordAuthenticationFilter}
 * but usage of this filter provides an ability to handle data of filter before this filter
 * starts working with these data.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UsernamePasswordAuthenticationFilter 
    extends org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter {
    
    private List<FilterPreHandler> preHandlers;
    
    /**
     * Constructs an instance of filter with passed prehandlers.
     * 
     * @param preHandlers these handlers must handle data of filter before this filter
     *                    starts working with its data.
     */
    public UsernamePasswordAuthenticationFilter(List<FilterPreHandler> preHandlers) {
        this.preHandlers = preHandlers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        for (FilterPreHandler preHandler: preHandlers) {
            preHandler.handle(request);
        }
        super.doFilter(req, res, chain);
    }
}
