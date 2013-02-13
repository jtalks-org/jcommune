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
package org.jtalks.jcommune.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.service.BannerService;
import org.jtalks.jcommune.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that handles all related to working with banners
 * http requests.
 * 
 * @author Anuar_Nurmakanov
 *
 */
@Controller
public class BannerController {
    private BannerService bannerService;
    private ComponentService componentService;

    /**
     * Constructs an instance with required fields.
     * 
     * @param bannerService to attach banner to concrete position on page
     */
    @Autowired
    public BannerController(BannerService bannerService, ComponentService componentService) {
        this.bannerService = bannerService;
        this.componentService = componentService;
    }
    
    /**
     * Upload banner. If banner exists it will override it value,
     * otherwise it will create new banner.
     * 
     * @param uploadedBanner uploaded banner 
     * @return url of requested page, so it will be re-displayed to user
     */
    @RequestMapping(value = "/banners/upload", method = RequestMethod.POST)
    public String upload(@ModelAttribute Banner uploadedBanner, HttpServletRequest request) {
        Component component = componentService.getComponentOfForum();
        bannerService.uploadBanner(uploadedBanner, component);
        return "redirect:" + getSourcePageUrl(request);
    }
    
    /**
     * Get url of source page from which request came.
     * 
     * @param request http request
     * @return url of source page
     */
    private String getSourcePageUrl(HttpServletRequest request) {
        return request.getHeader("referer");
    }
}
