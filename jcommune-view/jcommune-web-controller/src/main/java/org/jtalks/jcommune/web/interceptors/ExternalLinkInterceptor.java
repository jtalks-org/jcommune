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
package org.jtalks.jcommune.web.interceptors;

import org.jtalks.jcommune.model.entity.ExternalLink;
import org.jtalks.jcommune.service.ExternalLinkService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Global interceptor that works for all pages of the forum.
 * It gets all forum external links and put them to the model
 * that will be displayed page.
 *
 * @author Alexandre Teterin
 *         Date: 10.02.13
 */


public class ExternalLinkInterceptor extends HandlerInterceptorAdapter {
    static final String EXTERNAL_LINKS_MODEL_PARAM = "externalLinks";

    private ExternalLinkService externalLinkService;

    /**
     * Create an instance and initialize it fields.
     *
     * @param service {@link ExternalLink} related externalLinkService.
     */
    public ExternalLinkInterceptor(ExternalLinkService service) {
        this.externalLinkService = service;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        if (modelAndView != null) {
            List<ExternalLink> links = externalLinkService.getLinks();
            modelAndView.addObject(EXTERNAL_LINKS_MODEL_PARAM, links);
        }
    }
}
