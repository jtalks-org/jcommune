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

    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;

    private NoBodyResponseWrapper noBodyResponseWrapper;
    private HttpServletRequestWrapper forceRequestWrapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        this.httpServletRequest = (HttpServletRequest) request;
        this.httpServletResponse = (HttpServletResponse) response;

        if (isHeadTypeRequest(this.httpServletRequest)) {

            NoBodyResponseWrapper noBodyResponseWrapper = getNoBodyResponseWrapper();

            chain.doFilter(getForceRequestWrapper(), noBodyResponseWrapper);
            noBodyResponseWrapper.setContentLength();

        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isHeadTypeRequest(HttpServletRequest request) {
        return "HEAD".equals(request.getMethod());
    }

    public HttpServletRequestWrapper getForceRequestWrapper() {

        //@todo When uncomment out this condition application throw exception. Please somebody fix this.
        //if (this.forceRequestWrapper == null) {
           this.setForceRequestWrapper(new ForceGetRequestWrapper(this.httpServletRequest));
        //}

        return this.forceRequestWrapper;
    }

    public void setForceRequestWrapper(HttpServletRequestWrapper forceRequestWrapper) {
        this.forceRequestWrapper = forceRequestWrapper;
    }

    public NoBodyResponseWrapper getNoBodyResponseWrapper() {

        //@todo When uncomment out this condition application throw exception. Please somebody fix this.
        //if (this.noBodyResponseWrapper == null) {
            this.setNoBodyResponseWrapper(new NoBodyResponseWrapper(this.httpServletResponse));
       // }

        return this.noBodyResponseWrapper;
    }

    public void setNoBodyResponseWrapper(NoBodyResponseWrapper noBodyResponseWrapper) {
        this.noBodyResponseWrapper = noBodyResponseWrapper;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
