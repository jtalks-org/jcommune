/**
 * Copyright (C) 2011  jtalks.org Team
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
 * Also add information on how to contact you by electronic and paper mail.
 * Creation date: Apr 12, 2011 / 8:05:19 PM
 * The jtalks.org Project
 */
package org.jtalks.jcommune.web.util;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 *
 */
public class TimeZoneConversionInterceptor extends HandlerInterceptorAdapter {

    private static final String GMT_PARAM_NAME = "GMT";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getSession().getAttribute(GMT_PARAM_NAME)!= null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(GMT_PARAM_NAME)) {
                    int gmt = -Integer.parseInt(cookie.getValue()) / 60;
                    String customTimeZoneId = GMT_PARAM_NAME + (gmt > 0 ? "+" : "") + gmt;
                    TimeZone timeZone = TimeZone.getTimeZone(customTimeZoneId);
                    String canonicalTimeZoneId = TimeZone.getAvailableIDs(timeZone.getRawOffset())[0];
                    request.getSession().setAttribute(GMT_PARAM_NAME, canonicalTimeZoneId);
                }
            }
        }
        return super.preHandle(request, response, handler);
    }
}
