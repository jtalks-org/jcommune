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
package org.jtalks.jcommune.web.rememberme;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.codec.Base64;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;

import static org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices.SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;


/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class RememberMeCookieDecoderImplTest {
    private RememberMeCookieDecoder decoder = new RememberMeCookieDecoderImpl();
    
    @Test
    public void extractCookieShouldReturnValueOfRememberMeCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String expectedCookieValue = "encoded series and token";
        Cookie cookie = new Cookie(SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY, expectedCookieValue);
        request.setCookies(cookie);
        
        String actualCookieValue = decoder.exctractRememberMeCookieValue(request);
        
        assertEquals(actualCookieValue, expectedCookieValue, "Extract should return value of remember me cookie.");
    }
    
    @Test
    public void extractCookieShouldReturnNullFromRequestWithoutCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        String actualCookieValue = decoder.exctractRememberMeCookieValue(request);
        
        assertNull(actualCookieValue, "Extract should return null, because request doesn't contain cookies.");
    }
    
    @Test
    public void extractCookieShouldReturnNullForRequestWithoutRememberMeCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("name", "encoded series and token");
        request.setCookies(cookie);
        
        String actualCookieValue = decoder.exctractRememberMeCookieValue(request);
        
        assertNull(actualCookieValue, "Extract should return null, because request doesn't contain remember me cookie.");
    }
    
    @Test
    public void extractSeriesAndTokenShouldReturnThemFromEncodedCookieValue() {
        String expectedSeries = "61ikbvB7Nd1Wk3jDXgN/TQ==";
        String expectedToken = "FGGNNSS0KoIg7zO9+VlSaw==";
        String encodedCookieValue = encodeSeriesAndToken(expectedSeries, expectedToken);
        
        String[] actualSeriesAndToken = decoder.extractSeriesAndToken(encodedCookieValue);
        String actualSeries = actualSeriesAndToken[0];
        String actualToken = actualSeriesAndToken[1];
        
        assertEquals(actualSeries, expectedSeries);
        assertEquals(actualToken, expectedToken);
    }
    
    @Test
    public void extractSeriesAndTokenShouldReturnNothingForNotEncodedValue() {
        String notEncodedCookieValue = "*cookie-octet / ( DQUOTE *cookie-octet DQUOTE )";
        
        String[] actualSeriesAndToken = decoder.extractSeriesAndToken(notEncodedCookieValue);
        
        assertNull(actualSeriesAndToken);
    }
    
    /* copy-paste from AbstractRememberMeServices.encodeCookie */
    private String encodeSeriesAndToken(String series, String token) {
        String[] cookieTokens = new String[] {series, token};
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < cookieTokens.length; i++) {
            sb.append(cookieTokens[i]);

            if (i < cookieTokens.length - 1) {
                sb.append(":");
            }
        }

        String value = sb.toString();

        sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

        while (sb.charAt(sb.length() - 1) == '=') {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }
}
