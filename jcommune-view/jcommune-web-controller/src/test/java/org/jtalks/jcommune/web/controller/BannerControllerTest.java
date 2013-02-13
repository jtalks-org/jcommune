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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.jtalks.jcommune.model.entity.Banner;
import org.jtalks.jcommune.service.BannerService;
import org.jtalks.jcommune.service.ComponentService;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class BannerControllerTest {
    @Mock
    private BannerService bannerService;
    @Mock
    private ComponentService componentService;
    //
    private BannerController bannerController;
    
    @BeforeMethod
    public void init() {
        initMocks(this);
        bannerController = new BannerController(bannerService, componentService);
    }
    
    @Test
    public void afterBannerUploadingUserShouldStayInTheSamePage() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        String initialPage = "/topics/2";
        when(request.getHeader("referer")).thenReturn(initialPage);
        Banner uploadedBanner = new Banner();
        
        String resultUrl = bannerController.upload(uploadedBanner, request);
        
        assertEquals(resultUrl, "redirect:" + initialPage);
    }
}
