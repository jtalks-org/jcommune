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
 * 
 * @author Anuar_Nurmakanov
 *
 */
public class RememberMeLogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RememberMeLogService.class);
    private static final String NOT_EQUALS_TOKENS_ERROR_TEMPLATE = 
            "Presented token %s of series %s isn't equal for persistent token %s";
    
    private PersistentTokenRepository persistentTokenRepository;
    
    /**
     * 
     * @param persistentTokenRepository
     */
    public RememberMeLogService(PersistentTokenRepository persistentTokenRepository) {
        this.persistentTokenRepository = persistentTokenRepository;
    }

    /**
     * 
     * @param presentedSeries
     * @param presentedToken
     */
    public void logPersistentRememberMeToken(String presentedSeries, String presentedToken) {
        PersistentRememberMeToken token = persistentTokenRepository.getTokenForSeries(presentedSeries);
        if (token != null) {
            String persistentToken = token.getTokenValue();
            if (!ObjectUtils.equals(presentedToken, persistentToken)) {
                String errorMessage = String.format(
                        NOT_EQUALS_TOKENS_ERROR_TEMPLATE, presentedToken, presentedSeries, persistentToken);
                LOGGER.error(errorMessage);
            }
        }
    }
}
