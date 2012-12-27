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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

/**
 * Special trick implementation of "remember me" service that is needed to
 * decode cookie value. We use this trick to avoid copy-paste from Spring
 * implementation of decode method.
 * 
 * @author Anuar_Nurmakanov
 * 
 */
class RememberMeCookieDecoderImpl extends AbstractRememberMeServices implements RememberMeCookieDecoder {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLoginSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication successfulAuthentication) {
        throw new UnsupportedOperationException();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens,
            HttpServletRequest request, HttpServletResponse response)
            throws RememberMeAuthenticationException,
            UsernameNotFoundException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] decodeCookie(String cookieValue) throws InvalidCookieException {
        return super.decodeCookie(cookieValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractRememberMeCookie(HttpServletRequest request) {
        return super.extractRememberMeCookie(request);
    }
}