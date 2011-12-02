package org.jtalks.jcommune.web.interceptor;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interceptor simply breaks interceptor chain when processing static
 * resources. In this case we don't need all these complicated actions
 * like database access to serve the request.
 *
 * @author Evgeniy Naumenko
 */
public class ResourceHandlerInterceptor extends HandlerInterceptorAdapter {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        HttpRequestHandler resourcehandler = ((HttpRequestHandler) handler);
        resourcehandler.handleRequest(request, response);
        //break interceptor chain
        return false;
    }
}
