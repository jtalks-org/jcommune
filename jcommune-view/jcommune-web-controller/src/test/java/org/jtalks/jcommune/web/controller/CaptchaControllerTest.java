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

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import org.mockito.Mock;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.testng.Assert.assertNull;


public class CaptchaControllerTest {
    @Mock
    private Producer captchaProducer;

    private CaptchaController captchaController;
    
    private final String GENERATED_CAPTCHA_TEXT = "2356";
    private final int IMAGE_WIDTH = 100;
    private final int IMAGE_HEIGHT= 50;
    private final int IMAGE_TYPE = 1;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        captchaController = new CaptchaController(captchaProducer);
    }

    @Test
    public void testHandleRequestToCaptchaImage() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream out = mock(ServletOutputStream.class);
        HttpSession session = mock(HttpSession.class);

        when(captchaProducer.createText()).thenReturn(GENERATED_CAPTCHA_TEXT);
        when(captchaProducer.createImage(GENERATED_CAPTCHA_TEXT)).
                thenReturn(new BufferedImage(IMAGE_WIDTH,IMAGE_HEIGHT,IMAGE_TYPE));

        captchaController.handleRequest(response, out, session);

        //verify(response).setHeader("Pragma", "no-cache");
        verify(response).setContentType("image/jpeg");
        verify(session).setAttribute(Constants.KAPTCHA_SESSION_KEY, GENERATED_CAPTCHA_TEXT);
    }
}
