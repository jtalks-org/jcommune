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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.service.BannerService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Global interceptor that works for all pages of the forum.
 * It gets all banners of the forum and put them to the model
 * that will be displayed page.
 * 
 * @author Anuar_Nurmakanov
 */
public class BannerInterceptor extends HandlerInterceptorAdapter {
    static final String BANNERS_MODEL_PARAM = "banners";
    static final String UPLOADED_BANNER_MODEL_PARAM = "uploadedBanner";
    
    private BannerService bannerService;
    
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param bannerService to get all banners
     */
    public BannerInterceptor(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        if (modelAndView != null) {
            Map<String, Banner> allBanersOfTheForum = bannerService.getAllBanners();
            modelAndView.addObject("banners", allBanersOfTheForum);
            modelAndView.addObject("uploadedBanner", new Banner());
        }
    }
}
