package org.jtalks.jcommune.web.filters.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ForceGetRequestWrapper extends HttpServletRequestWrapper {

    public ForceGetRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String getMethod() {
        return "GET";
    }
}