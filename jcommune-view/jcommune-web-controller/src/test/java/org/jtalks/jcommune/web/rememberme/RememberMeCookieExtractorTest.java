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

import static org.mockito.MockitoAnnotations.initMocks;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class RememberMeCookieExtractorTest {
    @Mock
    private RememberMeCookieDecoder rememberMeCookieDecoder;
    private RememberMeCookieExtractor cookieExtractor;

    @BeforeMethod
    public void init() {
        initMocks(this);
        this.cookieExtractor = new RememberMeCookieExtractor(rememberMeCookieDecoder);
    }
    
    @Test
    public void exctractCookieValueShouldReturnFromRequest() {
        HttpServletRequest servletRequest = new MockHttpServletRequest();
        String expectedCookieValue = "expected cookie value";
        when(rememberMeCookieDecoder.extractRememberMeCookie(servletRequest))
            .thenReturn(expectedCookieValue);
        
        String actualCookieValue = cookieExtractor.exctractRememberMeCookieValue(servletRequest);
        
        assertEquals(actualCookieValue, expectedCookieValue);
    }
    
    @Test
    public void extractShoultReturnItFromCookieValue() {
        String cookieValue = "cookie value";
        String[] expectedSeriesAndToken = new String[] {"series", "token"};
        when(rememberMeCookieDecoder.decodeCookie(cookieValue))
            .thenReturn(expectedSeriesAndToken);
        
        String[] actualSeriesAndToken = cookieExtractor.extractSeriesAndToken(cookieValue);
        
        assertEquals(actualSeriesAndToken, expectedSeriesAndToken);
    }
    
    @Test
    public void extractWhenCookieInvalidShouldNotReturnSeriesAndToken() {
        String cookieValue = "cookie value";
        when(rememberMeCookieDecoder.decodeCookie(cookieValue))
            .thenThrow(new InvalidCookieException("message"));
        
        String[] actualSeriesAndToken = cookieExtractor.extractSeriesAndToken(cookieValue);
        
        int emptyResult = 0;
        assertEquals(actualSeriesAndToken.length, emptyResult);
    }
}
