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

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

/**
 * Implements our custom Remember Me service to replace the Spring default one. This implementation removes Remember Me
 * token only for a single session.
 * <p><b>Justification:</b> Spring's * {@link PersistentTokenBasedRememberMeServices} removes all the tokens from DB
 * for a user whose session expired - even the sessions started on a different machine or device. Thus users were
 * frustrated when their sessions expired on the machines where the Remember Me checkbox was checked.
 * </p>
 */
public class RememberMeServices extends PersistentTokenBasedRememberMeServices {
    private final static String REMOVE_TOKEN_QUERY = "DELETE FROM persistent_logins WHERE series = ? AND token = ?";
    private final static int TOCKEN_CACHE_MAX_SIZE = 500;
    private final RememberMeCookieDecoder rememberMeCookieDecoder;
    private final JdbcTemplate jdbcTemplate;
    private final Queue<PersistentRememberMeToken> tokenCache = new ConcurrentLinkedQueue<>();
    private PersistentTokenRepository tokenRepository;

    /**
     * @param rememberMeCookieDecoder needed for extracting rememberme cookies
     * @param jdbcTemplate            needed to execute the sql queries
     * @throws Exception - see why {@link PersistentTokenBasedRememberMeServices} throws it
     */
    public RememberMeServices(RememberMeCookieDecoder rememberMeCookieDecoder, JdbcTemplate jdbcTemplate)
            throws Exception {
        super();
        this.rememberMeCookieDecoder = rememberMeCookieDecoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Causes a logout to be completed. The method must complete successfully.
     * Removes client's token which is extracted from the HTTP request.
     * {@inheritDoc}
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String cookie = rememberMeCookieDecoder.exctractRememberMeCookieValue(request);
        if (cookie != null) {
            String[] seriesAndToken = rememberMeCookieDecoder.extractSeriesAndToken(cookie);
            if (logger.isDebugEnabled()) {
                logger.debug("Logout of user " + (authentication == null ? "Unknown" : authentication.getName()));
            }
            cancelCookie(request, response);
            jdbcTemplate.update(REMOVE_TOKEN_QUERY, seriesAndToken);
            removeTokenFromCache(seriesAndToken);
        }
    }
    
    /**
     * Temporary solution to localize "remember-me" authentication bug. 
     * Recently used tokens stores in cache. If CookieTheftException was thrown, performed search of tokens in cache. 
     * {@inheritDoc}
     */
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
         if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
         
        final String presentedSeries = cookieTokens[0];
        final PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);
        
         if (token == null) {
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }
        cacheToken(token);
        
        try {
            final UserDetails userDetails = super.processAutoLoginCookie(cookieTokens, request, response); 
            return userDetails;
            
        } catch (CookieTheftException cte) {
            
            logger.debug("Search for token in token cache");
            
            final PersistentRememberMeToken cachedTocken = findTockenInCache(cookieTokens);
            if (cachedTocken == null) {
                if (logger.isDebugEnabled()) {
                logger.debug("Token with series '" + cachedTocken.getSeries() + "' and value '"
                        + cachedTocken.getTokenValue() + "' not found in cache");
                }
                throw cte;
            }
            
            if (logger.isDebugEnabled()) {
                logger.debug("Token for user '" + cachedTocken.getUsername() + "', series '" +
                    cachedTocken.getSeries() + "' found in cache");
            }
            try {
                tokenRepository.createNewToken(cachedTocken);
                setCookie(new String[] {cachedTocken.getSeries(), cachedTocken.getTokenValue()}, getTokenValiditySeconds(),
                        request, response);
            } catch (DataAccessException e) {
                 logger.error("Failed to save persistent token ", e);
            }
            return getUserDetailsService().loadUserByUsername(cachedTocken.getUsername());
        }
    }
    
    /**
    * {@inheritDoc}
    */
    @Override
    public void setTokenRepository(PersistentTokenRepository tokenRepository) {
        super.setTokenRepository(tokenRepository);
        this.tokenRepository = tokenRepository;
    }
    

    private void cacheToken(PersistentRememberMeToken token) {
        if (tokenCache.size() == TOCKEN_CACHE_MAX_SIZE) {
            tokenCache.poll();
        }
        tokenCache.add(token);
    }
    
    private PersistentRememberMeToken findTockenInCache(String[] cookieTokens) {
        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];
        for (final PersistentRememberMeToken token : tokenCache) {
            if (presentedToken.equals(token.getTokenValue()) && presentedSeries.equals(token.getSeries())) {
                return token;
            }
        }
        return null;
    }
    
    private void removeTokenFromCache(String[] seriesAndToken) {
        final String presentedSeries = seriesAndToken[0];
        final String presentedToken = seriesAndToken[1];
        for (final PersistentRememberMeToken token : tokenCache) {
            if (presentedToken.equals(token.getTokenValue()) && presentedSeries.equals(token.getSeries())) {
                tokenCache.remove(token);
            }
        }
    }
}