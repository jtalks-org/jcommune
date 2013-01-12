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

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Mock;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class UsernamePasswordAuthenticationFilterTest {
    @Mock
    private FilterPreHandler firstFilterPreHandler;
    @Mock
    private FilterPreHandler secondFilterPreHandler;
    //
    private UsernamePasswordAuthenticationFilter filter;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        filter = new UsernamePasswordAuthenticationFilter(
                Arrays.asList(firstFilterPreHandler, secondFilterPreHandler));
    }
    
    @Test
    public void filterShouldCallPreHandlers() throws IOException, ServletException {
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse responce = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();
        
        filter.doFilter(request, responce, filterChain);
        
        verify(firstFilterPreHandler).handle(request);
        verify(secondFilterPreHandler).handle(request);
    }
}
