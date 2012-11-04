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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class TrimFilterTest {
    private static final String EXCLUDE_PATTERN = "^.*/resources/.*";
    private MockHttpServletRequest request = new MockHttpServletRequest();
    private MockHttpServletResponse response = new MockHttpServletResponse();
    private MockFilterConfig filterConfig = new MockFilterConfig();
    @Mock
    private Filter wrappedTrimFilter;
    @Mock
    private FilterChain filterChain;
    //
    private TrimFilter trimFilter;
    
    @BeforeTest
    private void init() {
        MockitoAnnotations.initMocks(this);
        this.trimFilter = new TrimFilter(wrappedTrimFilter);
    }
    
    @Test
    public void testInit() throws ServletException {
        FilterConfig filterConfig = Mockito.mock(FilterConfig.class);
        trimFilter.init(filterConfig);
        
        Mockito.verify(wrappedTrimFilter).init(filterConfig);
        Mockito.verify(filterConfig).getInitParameter(TrimFilter.EXCLUDE_PATTERNS_INIT_PARAMETER);
    }
    
    @Test
    public void testDoFilterWithPossibleUrl() throws IOException, ServletException {
        String uri = "/jcommune/";
        request.setRequestURI(uri);
        filterConfig.addInitParameter(TrimFilter.EXCLUDE_PATTERNS_INIT_PARAMETER, EXCLUDE_PATTERN);
        trimFilter.init(filterConfig);
        
        trimFilter.doFilter(request, response, filterChain);
        
        Mockito.verify(wrappedTrimFilter).doFilter(request, response, filterChain);
        
    }
    
    @Test
    public void testDoFilterWithNotPossibleUrl() throws IOException, ServletException {
        String uri = "/jcommune/resources/javascript/licensed/jquery/jquery.prettyPhoto.js";
        request.setRequestURI(uri);
        filterConfig.addInitParameter(TrimFilter.EXCLUDE_PATTERNS_INIT_PARAMETER, EXCLUDE_PATTERN);
        trimFilter.init(filterConfig);
        
        trimFilter.doFilter(request, response, filterChain);
        
        Mockito.verify(filterChain).doFilter(request, response);
    }
    
    @Test
    public void testDestroy() {
        trimFilter.destroy();
        
        Mockito.verify(wrappedTrimFilter).destroy();
    }
}
