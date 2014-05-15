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

import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 * @author Mikhail Stryzhonok.
 */
public class ThrottlingRememberMeServiceTest {
    private static final String PRESENTED_SERIES = "61ikbvB7Nd1Wk3jDXgN/TQ==";
    private static final String PRESENTED_TOKEN = "FGGNNSS0KoIg7zO9+VlSaw==";

    @Mock
    private PersistentTokenRepository tokenRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetails details;

    @Mock
    private UserDetailsService detailsService;

    @Spy
    private ThrottlingRememberMeService services;


    private PersistentRememberMeToken token = new PersistentRememberMeToken("user", PRESENTED_SERIES, PRESENTED_TOKEN, new Date());


    @BeforeMethod
    public void init() throws Exception{
        services = new ThrottlingRememberMeService(null, null);
        initMocks(this);

        doReturn(details).when(services).loginWithSpringSecurity(any(String[].class),any(HttpServletRequest.class), any(HttpServletResponse.class));
        doNothing().when(services).rewriteCookie(eq(token), any(HttpServletRequest.class), any(HttpServletResponse.class));

        when(tokenRepository.getTokenForSeries(PRESENTED_SERIES)).thenReturn(token);
        when(detailsService.loadUserByUsername(token.getUsername())).thenReturn(details);

        services.setTokenRepository(tokenRepository);
        services.setUserDetailsService(detailsService);
    }

    @Test
    public void loginWithSpringSecurityShouldBeCalledOnceWhenTokenValid() {
        services.setCachedTokenValidityTime(5000);
        services.processAutoLoginCookie(new String[]{PRESENTED_SERIES, PRESENTED_TOKEN}, null, null);
        services.processAutoLoginCookie(new String[]{PRESENTED_SERIES, PRESENTED_TOKEN}, null, null);
        verify(services, times(1)).loginWithSpringSecurity(eq(new String[]{PRESENTED_SERIES, PRESENTED_TOKEN}),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void loginWithSpringSecurityShouldBeCalledTwoTimesWhenTokenInvalid() throws Exception{
        services.setCachedTokenValidityTime(1000);
        services.processAutoLoginCookie(new String[]{PRESENTED_SERIES, PRESENTED_TOKEN}, null, null);
        Thread.sleep(1500);
        services.processAutoLoginCookie(new String[]{PRESENTED_SERIES, PRESENTED_TOKEN}, null, null);
        verify(services, times(2)).loginWithSpringSecurity(eq(new String[]{PRESENTED_SERIES, PRESENTED_TOKEN}),
                any(HttpServletRequest.class), any(HttpServletResponse.class));
    }
}
