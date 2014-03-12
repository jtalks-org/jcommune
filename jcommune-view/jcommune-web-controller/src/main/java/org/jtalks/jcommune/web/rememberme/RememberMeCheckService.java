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

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * Provides an ability to check remember me data that were passed in cookie value.
 * We need this functionality for cases when user can't log in using remember me cookie
 * in application. So we catch such kind of cases and log all data that can help
 * to find the reason of root problem.
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class RememberMeCheckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RememberMeCheckService.class);
    private static final String NOT_EQUALS_TOKENS_ERROR_TEMPLATE = 
            "%s presented token %s of series %s isn't equal for persistent token %s";
    
    private final PersistentTokenRepository persistentTokenRepository;
    
    /**
     * Constructs an instance with required fields.
     * 
     * @param persistentTokenRepository to find token in repository
     */
    public RememberMeCheckService(PersistentTokenRepository persistentTokenRepository) {
        this.persistentTokenRepository = persistentTokenRepository;
    }

    /**
     * Find and check found persistent remember me token with presented token
     * from cookie.
     * 
     * @param presentedSeries presented series from cookie
     * @param presentedToken presented token from cookie
     * @return {@code true} if token not exists in database or presented token equals to persistent token,
     *         {@code false} if token exists in database and it doesn't equals to presented token
     */
    public boolean equalWithPersistentToken(String presentedSeries, String presentedToken) {
        PersistentRememberMeToken token = persistentTokenRepository.getTokenForSeries(presentedSeries);
        if (token != null) {
            String persistentToken = token.getTokenValue();
            if (!ObjectUtils.equals(presentedToken, persistentToken)) {
                String logErrorMessage = composeErrorMessageForNotEqualTokens(
                        token.getUsername(), presentedToken, presentedSeries, persistentToken);
                LOGGER.error(logErrorMessage);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Compose error message for case when token isn't equal token from database.
     * 
     * @param username owner of token, it's given from database
     * @param presentedToken token from cookie
     * @param presentedSeries series from cookie
     * @param persistentToken token from database
     * @return error message for given token details
     */
    private String composeErrorMessageForNotEqualTokens(
            String username,
            String presentedToken,
            String presentedSeries,
            String persistentToken) {
        return String.format(
                NOT_EQUALS_TOKENS_ERROR_TEMPLATE,
                username,
                presentedToken,
                presentedSeries,
                persistentToken);
    }
}
