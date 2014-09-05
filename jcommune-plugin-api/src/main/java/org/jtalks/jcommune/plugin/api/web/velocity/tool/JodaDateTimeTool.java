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
package org.jtalks.jcommune.plugin.api.web.velocity.tool;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Custom tool to format {@link org.joda.time.DateTime} object in velocity templates.
 * {@link org.apache.velocity.tools.generic.DateTool} not supports {@link org.joda.time.DateTime}
 * see <a href="https://issues.apache.org/jira/browse/VELTOOLS-135">VELTOOLS-135</a>
 *
 * @author Mikhail Stryzhonok
 */
public class JodaDateTimeTool {
    private static final String DATE_FORMAT_PATTERN = "dd MMM yyyy HH:mm";
    public static final String GMT_COOKIE_NAME = "GMT";
    public static final int DEFAULT_OFFSET = 0;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT_PATTERN);
    private long offset = DEFAULT_OFFSET;

    public JodaDateTimeTool(HttpServletRequest request) {
        if (request != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(GMT_COOKIE_NAME)) {
                        offset = this.convertTimeZoneOffset(cookie.getValue());
                        break;
                    }
                }
            }
        }
    }

    public String format(DateTime dateTime, Locale locale) {
        if (dateTime == null) {
            return "";
        }
        DateTimeZone timeZone = dateTime.getZone();
        long utcTime = timeZone.convertLocalToUTC(dateTime.getMillis(), false);
        dateTime = new DateTime(utcTime + offset);
        return formatter.withLocale(locale).print(dateTime);
    }

    /**
     * Converts timezone offset representation to millisecond offset.
     * If timezone offset representation is incorrect, then GMT
     * timezone value is used.
     *
     * @param jsRepresentation time difference between GMT and
     *                         local time, in minutes. Example: "-120"
     * @return signed millisecond timezone offset
     */
    private long convertTimeZoneOffset(String jsRepresentation) {
        try {
            int offsetInMinutes = -Integer.parseInt(jsRepresentation);
            return TimeUnit.MINUTES.toMillis(offsetInMinutes);
        } catch (NumberFormatException e) {
            // someone has passed wrong offset in cookie, use GMT
            return DEFAULT_OFFSET;
        }
    }
}
