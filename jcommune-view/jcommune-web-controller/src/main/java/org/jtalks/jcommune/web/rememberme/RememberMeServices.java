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

import com.google.common.annotations.VisibleForTesting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    // We should store a lot of tokens to prevent cache overflow
    private static final int TOKEN_CACHE_MAX_SIZE = 100;
    private final RememberMeCookieDecoder rememberMeCookieDecoder;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String, PersistentRememberMeTokenWrapper> tokenCache = new ConcurrentHashMap<>();
    private PersistentTokenRepository tokenRepository = new InMemoryTokenRepositoryImpl();
    // 5 seconds should be enough for processing request and sending response to client
    private int cachedTokenValidityTime = 5 * 1000;

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
            tokenCache.remove(seriesAndToken[0]);
            validateTokenCache();
        }
    }

    /**
     * Solution for preventing "remember-me" bug. Some browsers sends preloading requests to server to speed-up
     * page loading. It may cause error when response of preload request not returned to client and second request
     * from client was send. This method implementation stores token in cache for <link>CACHED_TOKEN_VALIDITY_TIME</link>
     * milliseconds and check token presence in cache before process authentication. If there is no equivalent token in
     * cache authentication performs normally. If equivalent present in cache we should not update token in database.
     * This approach can provide acceptable security level and prevent errors.
     * {@inheritDoc}
     * @see <a href="http://jira.jtalks.org/browse/JC-1743">JC-1743</a>
     * @see <a href="https://developers.google.com/chrome/whitepapers/prerender?csw=1">Page preloading in Google Chrome</a>
     */
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        if (cookieTokens.length != 2) {
            throw new InvalidCookieException("Cookie token did not contain " + 2 +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        final String presentedSeries = cookieTokens[0];
        final String presentedToken = cookieTokens[1];

        PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);

        if (token == null) {
            // No series match, so we can't authenticate using this cookie
            throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
        }

        UserDetails details = null;

        if (isTokenCached(presentedSeries, presentedToken)) {
            details = getUserDetailsService().loadUserByUsername(token.getUsername());
            rewriteCookie(token, request, response);
        } else {
            /* IMPORTANT: We should store token in cache before calling <code>loginWithSpringSecurity</code> method.
               Because execution of this method can take a long time.
             */
            cacheToken(token);
            details =  loginWithSpringSecurity(cookieTokens, request, response);
        }
        validateTokenCache();

        return details;
    }

    /**
     * Calls PersistentTokenBasedRememberMeServices#processAutoLoginCookie method.
     * Needed for possibility to test.
     */
    @VisibleForTesting
    UserDetails loginWithSpringSecurity(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {
        return super.processAutoLoginCookie(cookieTokens, request, response);
    }

    /**
     * Sets valid cookie to response/
     * Needed for possibility to test.
     */
    @VisibleForTesting
    void rewriteCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
        setCookie(new String[] {token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request, response);
    }

    @Override
    public void setTokenRepository(PersistentTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        super.setTokenRepository(tokenRepository);
    }

    /**
     * Stores token in cache.
     * @param token Token to be stored
     * @see org.jtalks.jcommune.web.rememberme.PersistentRememberMeTokenWrapper
     */
    private void cacheToken(PersistentRememberMeToken token) {
        if (tokenCache.size() >= TOKEN_CACHE_MAX_SIZE) {
            validateTokenCache();
        }
        PersistentRememberMeTokenWrapper tokenWrapper = new PersistentRememberMeTokenWrapper(token.getTokenValue(), System.currentTimeMillis());
        tokenCache.put(token.getSeries(), tokenWrapper);
    }

    /**
     * Removes from cache tokens which were stored more than <link>CACHED_TOKEN_VALIDITY_TIME</link> milliseconds ago.
     */
    private void validateTokenCache() {
        for (Map.Entry<String, PersistentRememberMeTokenWrapper> entry: tokenCache.entrySet()) {
            if (!isTokenWrapperValid(entry.getValue())) {
                tokenCache.remove(entry);
            }
        }
    }

    /**
     * Checks if given tokenWrapper valid.
     * @param tokenWrapper Token wrapper to be checked
     * @return <code>true</code> tokenWrapper was stored in cache less than <link>CACHED_TOKEN_VALIDITY_TIME</link> milliseconds ago.
     * <code>false</code> otherwise.
     * @see org.jtalks.jcommune.web.rememberme.PersistentRememberMeTokenWrapper
     */
    private boolean isTokenWrapperValid(PersistentRememberMeTokenWrapper tokenWrapper) {
        if ((System.currentTimeMillis() - tokenWrapper.getCachingTime()) >= cachedTokenValidityTime) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if token with given series and value stored in cache
     * @param series series to be checked
     * @param value value to be checked
     * @return <code>true</code> if token stored in cache< <code>false</code> otherwise.
     */
    private boolean isTokenCached(String series, String value) {
        if (tokenCache.containsKey(series) && isTokenWrapperValid(tokenCache.get(series))
                && value.equals(tokenCache.get(series).getValue())) {
            return true;
        }
        return false;
    }

    public void setCachedTokenValidityTime(int cachedTokenValidityTime) {
        this.cachedTokenValidityTime = cachedTokenValidityTime;
    }
}