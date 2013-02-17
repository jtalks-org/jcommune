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

import org.jtalks.jcommune.service.ExternalLinkService;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.ModelAndViewAssert.assertModelAttributeAvailable;

/**
 * @author Alexandre Teterin
 *         Date: 16.02.13
 */


public class ExternalLinkInterceptorTest {

    @Mock
    private ExternalLinkService service;
    private ExternalLinkInterceptor interceptor;

    @BeforeMethod
    public void setUp() {
        initMocks(this);
        interceptor = new ExternalLinkInterceptor(service);
    }

    @Test
    public void testPostHandle() throws Exception {
        ModelAndView mav = new ModelAndView("mav");
        interceptor.postHandle(null, null, null, mav);

        assertModelAttributeAvailable(mav, ExternalLinkInterceptor.EXTERNAL_LINKS_MODEL_PARAM);
    }
}
