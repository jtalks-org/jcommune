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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private final static Logger LOGGER = LoggerFactory.getLogger(RememberMeServices.class);
    private final static String REMOVE_TOKEN_QUERY = "DELETE FROM persistent_logins WHERE series = ? AND token = ?";
    
    /**
     * We need provide RememberMeServices sufficiently large cache to hold information about 
     * a lot of requests, because "remember-me" authentication bug is rarely reproduced.
     * @see <a href="http://jira.jtalks.org/browse/JC-1743">JIRA issue</a>
     */
    private final static int TOKEN_CACHE_MAX_SIZE = 500;
    private final static String BROWSER_VERSION_REQUEST_HEADER = "User-Agent";
    private final static String IP_ADDESS_HEADER = "X-FORWARDED-FOR";
    
    private final RememberMeCookieDecoder rememberMeCookieDecoder;
    private final JdbcTemplate jdbcTemplate;
    private final Queue<UserInfo> userInfoCache = new ConcurrentLinkedQueue<>();
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
            removeUserInfoFromCache(seriesAndToken[0]);
        }
    }
    
    /**
     * Temporary solution to localize "remember-me" authentication bug. 
     * Recently used tokens and information about users, who use this tokens  stores in cache. 
     * If CookieTheftException was thrown, performed search of user info in cache. 
     * {@inheritDoc}
     * @see <a href="http://jira.jtalks.org/browse/JC-1743">JIRA issue</a>
     */
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }
         
        String presentedSeries = cookieTokens[0];
        PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);
         if (token == null) {
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }
        UserInfo info = new UserInfo(token, getClientIpAddress(request), request.getRequestURI(), System.currentTimeMillis(),
                request.getHeader(BROWSER_VERSION_REQUEST_HEADER), request.getLocale());
        cacheUserInfo(info);
        
        try {
            UserDetails userDetails = super.processAutoLoginCookie(cookieTokens, request, response); 
            return userDetails;
            
        } catch (CookieTheftException cte) {

            LOGGER.debug("Search for user info in cache");
            UserInfo cachedInfo = findUserInfoInCache(cookieTokens);
            if (cachedInfo == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Equivalent user info not found in cache. Current user info is {}", info);
                }
                throw cte;
            }
            
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("User info {} found in cache", cachedInfo);
            }
            try {
                tokenRepository.createNewToken(cachedInfo.getToken());
                setCookie(new String[] {cachedInfo.getToken().getSeries(), cachedInfo.getToken().getTokenValue()}, 
                        getTokenValiditySeconds(), request, response);
            } catch (DataAccessException e) {
                 LOGGER.error("Failed to save persistent token ", e);
            }
            return getUserDetailsService().loadUserByUsername(cachedInfo.getToken().getUsername());
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
    

    private void cacheUserInfo(UserInfo info) {
        if (userInfoCache.size() >= TOKEN_CACHE_MAX_SIZE) {
            userInfoCache.poll();
        }
        userInfoCache.add(info);
    }
    
    private UserInfo findUserInfoInCache(String[] cookieTokens) {
        String presentedSeries = cookieTokens[0];
        String presentedToken = cookieTokens[1];
        for (UserInfo info : userInfoCache) {
            if (presentedToken.equals(info.getToken().getTokenValue()) 
                    && presentedSeries.equals(info.getToken().getSeries())) {
                return info;
            }
        }
        return null;
    }
    
    private void removeUserInfoFromCache(String presentedSeries) {
        for (UserInfo info : userInfoCache) {
            if (presentedSeries.equals(info.getToken().getTokenValue())) {
                userInfoCache.remove(info);
            }
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader(IP_ADDESS_HEADER);
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}