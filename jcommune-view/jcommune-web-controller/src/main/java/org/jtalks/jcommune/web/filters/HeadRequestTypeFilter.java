package org.jtalks.jcommune.web.filters;

import org.jtalks.jcommune.web.filters.wrapper.ForceGetRequestWrapper;
import org.jtalks.jcommune.web.filters.wrapper.NoBodyResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for process head http request.
 * Catch it and set GET request the default type and send empty response body according to HTTP standard
 */
public class HeadRequestTypeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if (isHeadTypeRequest(httpServletRequest)) {

            NoBodyResponseWrapper noBodyResponseWrapper = new NoBodyResponseWrapper(httpServletResponse);

            chain.doFilter(new ForceGetRequestWrapper(httpServletRequest), noBodyResponseWrapper);
            noBodyResponseWrapper.setContentLength();

        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isHeadTypeRequest(HttpServletRequest request) {
        return "HEAD".equals(request.getMethod());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
