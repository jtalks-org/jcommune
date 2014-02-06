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
package org.jtalks.jcommune.web.tags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * The class contains functions for the work with urls.
 */
public class UrlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlUtil.class);

    /**
     * The function encode url.
     *
     * @param url not encoded string of url,
     * @return encoded string.
     */
    public static String encodeUrl(String url) {
        if (url != null) {
            try {
                return URLEncoder.encode(url, "UTF-8").replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Could not encode url '{}'", url);
            }
        }
        return "";
    }

}
