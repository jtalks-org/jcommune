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
package org.jtalks.jcommune.model.validation;


import com.google.code.kaptcha.Constants;
import org.jtalks.jcommune.model.validation.validators.CaptchaValidator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CaptchaValidatorTest {

    private CaptchaValidator captchaValidator;
    
    private final String CAPTCHA_TEXT = "2234";

    @BeforeMethod
    public void init() {
        captchaValidator = new CaptchaValidator();
    }

    @Test
    public void testCoincidenceCaptchaWithInputValue(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, CAPTCHA_TEXT);
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertTrue(captchaValidator.isValid(CAPTCHA_TEXT, null));
    }

    @Test
    public void emptyValueIsAlwaysInvalidEvenIfExpectedIsEmpty(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, "");
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertFalse(captchaValidator.isValid("", null));
    }

    @Test
    public void nullValueIsAlwaysInvalidEvenIfExpectedIsEmpty(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, "");
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertFalse(captchaValidator.isValid(null, null));
    }

    @Test
    public void testNoCoincidenceCaptchaWithInputValue(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, "1234");
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        assertFalse(captchaValidator.isValid(CAPTCHA_TEXT, null));
    }
}
