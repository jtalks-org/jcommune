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
import java.util.TimeZone;

/**
 * This interceptor is used to convert user's timezone id obtained from
 * client side to canonical id. It was designed to keep conversion logic out from JSP page.
 * <p/>
 * If there are any processing error no exception is thrown, host server
 * timezone is used instead.
 *
 * @author Evgeniy Naumenko
 */
public class TimeZoneConversionInterceptor extends HandlerInterceptorAdapter {

    public static final String GMT_PARAM_NAME = "GMT";

    /**
     * Intercepts request handling to obtain timezone information set in cookies
     * by user agent
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param handler  chosen handler to execute, for type and/or instance evaluation
     * @return this implementation always returns true
     * @throws Exception from the next interceptors from the chain
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getSession().getAttribute(GMT_PARAM_NAME) == null) {
            // default value will be overwritten if found
            this.setGmtAttribute(request, this.getDefaultTimezoneId());
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(GMT_PARAM_NAME)) {
                        String convertedId = this.convertTimeZoneId(cookie.getValue());
                        this.setGmtAttribute(request, convertedId);
                        break;
                    }
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * Converts timezone offset representation to canonical timezone id.
     * If timezone offset representation is incorrect, then host server
     * timezone value is used.
     *
     * @param jsRepresentation time difference between GMT and
     *                         local time, in minutes. Example: "-120"
     * @return canonical timezone id for Joda Time. Example: "America/Los_Angeles"
     */
    private String convertTimeZoneId(String jsRepresentation) {
        try {
            int gmt = -Integer.parseInt(jsRepresentation) / 60;
            String customTimeZoneId = GMT_PARAM_NAME + this.getSignedIntString(gmt);
            TimeZone timeZone = TimeZone.getTimeZone(customTimeZoneId);
            /*
            The same GMT offset may be mapped on the different canonical ids.
            We don't care which one is used until it is mapped on the right GMT offset.
            */
            return TimeZone.getAvailableIDs(timeZone.getRawOffset())[0];
        } catch (NumberFormatException e) {
            // someone has passed wrong GMT in cookie
            return this.getDefaultTimezoneId();
        }
    }

    /**
     * Sets session attribute under GMT_PARAM_NAME key.
     * This attribute indicates timezone
     *
     * @param request request to get session from
     * @param value   string to be set as attribute value
     */
    private void setGmtAttribute(HttpServletRequest request, String value) {
        request.getSession().setAttribute(GMT_PARAM_NAME, value);
    }

    /**
     * Returns default time zone canonical id
     *
     * @return host server time zone canonical id
     */
    private String getDefaultTimezoneId() {
        return TimeZone.getDefault().getID();
    }

    /**
     * Returns string representation of the int with explicit sign
     *
     * @param value int to be converted
     * @return "+value" or "-value" string
     */
    private String getSignedIntString(int value) {
        if (value > 0) {
            return "+" + value;
        } else {
            return String.valueOf(value);
        }
    }
}
