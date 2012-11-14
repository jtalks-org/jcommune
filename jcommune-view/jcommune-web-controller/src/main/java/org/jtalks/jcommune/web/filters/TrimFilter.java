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
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.cj.trim.trimFilter;

/**
 * This filter provides the same functional as {@link trimFilter}, 
 * but it has additional option in configuration. It provides an ability
 * to exclude some pages by pattern. So all pages that matches this pattern
 * will be skipped by this filter.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class TrimFilter implements Filter {

    static final String EXCLUDE_PATTERNS_INIT_PARAMETER = "excludePatterns";
    private Filter wrappedTrimFilter;
    private String excludePatterns;
    
    /**
     * Default constructor. It's used by container. 
     */
    public TrimFilter() {
        this.wrappedTrimFilter = new trimFilter();
    }
    
    /**
     * Constructs an instance with required fields.
     * It gives an ability to wrap an exists implementation
     * of trim filter.
     * 
     * @param wrappedTrimFilter trim filter, that will be invoked 
     *                          in this filter
     */
    public TrimFilter(Filter wrappedTrimFilter) {
        this.wrappedTrimFilter = wrappedTrimFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.wrappedTrimFilter.init(filterConfig);
        this.excludePatterns = filterConfig.getInitParameter(EXCLUDE_PATTERNS_INIT_PARAMETER);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
        throws IOException, ServletException {
        
        String url = getRequestUrl(request);
        boolean isExcluded = isInExcludeUrls(url);
        if (!isExcluded) {
            wrappedTrimFilter.doFilter(request, response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Get url of request. 
     * 
     * @param request request
     * @return url if request is {@link HttpServletRequest} 
     *         it returns ulr of request, otherwise it returns empty string
     */
    private String getRequestUrl(ServletRequest request) {
        StringBuffer requestURL = ((HttpServletRequest)request).getRequestURL();
        return requestURL.toString(); 
    }
    
    /**
     * Checks whether the URL is in the exclusion list.
     * 
     * @param url the URL that will be checked
     * @return {@code true} if the URL is in the exclusion list,
     *         otherwise {@code false}
     */
    private boolean isInExcludeUrls(String url) {
        return excludePatterns != null && 
                url.matches(excludePatterns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        this.wrappedTrimFilter.destroy();
    }
}
