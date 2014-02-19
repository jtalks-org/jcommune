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

import java.util.Locale;
import org.joda.time.DateTime;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

/**
 * We need this class to hold in cache more info about users which gets
 * CookieTheftException.
 *
 * @see <a href="http://jira.jtalks.org/browse/JC-1743">JIRA issue</a>
 *
 * @author Mikhail Stryzhonok
 */
class UserInfo {

    private static final String STRING_FORMAT = "User info: name - %s, ip-addres - %s, browser ver. - %s, "
            + "requested URL - %s locale - %s. Token info: value - %s, series - %s, expire date - %s, "
            + "holded in cache - %s";

    private final PersistentRememberMeToken token;
    private final String ipAddr;
    private final String requestedUrl;
    private final DateTime dateTime;
    private final String browserVersion;
    private final Locale locale;

    public UserInfo(PersistentRememberMeToken token, String ipAddr, String requestedUrl,
            long currentMils, String browserVersion, Locale locale) {
        if (token == null || token.getSeries() == null || token.getTokenValue() == null) {
            throw new IllegalArgumentException("Persistent remember me token should be initialized");
        }
        this.token = token;
        this.ipAddr = ipAddr;
        this.requestedUrl = requestedUrl;
        this.dateTime = new DateTime(currentMils);
        this.browserVersion = browserVersion;
        this.locale = locale;
    }

    public PersistentRememberMeToken getToken() {
        return token;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public String getRequestedUrl() {
        return requestedUrl;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return String.format(STRING_FORMAT, token.getUsername(), ipAddr, browserVersion, requestedUrl, 
                locale, token.getTokenValue(), token.getSeries(), token.getDate(), dateTime);
    }
}
