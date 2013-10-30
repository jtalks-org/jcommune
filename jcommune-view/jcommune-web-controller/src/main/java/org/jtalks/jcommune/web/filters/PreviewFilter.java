package org.jtalks.jcommune.web.filters;

import javax.servlet.*;
import java.io.IOException;

public class PreviewFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        System.out.println(req.getParameter("preview"));
        System.out.println(req.getParameterMap().get("bodyText"));
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
