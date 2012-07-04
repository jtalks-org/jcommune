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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Serves captcha-related  http requests
 *
 * @author  Vitaliy Kravchenko
 */
@Controller
public class CaptchaController {

    private Producer captchaProducer;

    /**
     * Constructor creates MVC controller for captcha
     *
     * @param captchaProducer autowired object for generating captcha image
     */
    @Autowired
    public CaptchaController(Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    /**
     * This method obtains request for getting captcha image
     *
     * @param response  http response
     * @param out       servlet output stream
     * @param session   http session
     * @throws IOException when image cannot be written to output stream
     */
    @RequestMapping(value = "/captcha/image")
    public void handleRequest(HttpServletResponse response,
            ServletOutputStream out,HttpSession session) throws IOException {

        response.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = captchaProducer.createImage(capText);
        ImageIO.write(bi, "jpg", out);
        out.flush();
    }
}
