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
package org.jtalks.jcommune.web.util;

import org.apache.commons.lang.StringUtils;

/**
 * @author denis.berezhnoy
 */
public class RssUtils {
    /**
     * Method for skip invalid characters in content for RSS feed.
     * <p/>
     * Description valid chars in XML specification
     * http://www.w3.org/TR/REC-xml/#charsets
     *
     * @param in - post content.
     */
    public static String skipInValidXMLChars(String in) {
        if (StringUtils.isBlank(in)) return StringUtils.EMPTY;
        String pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
        return in.replaceAll(pattern, StringUtils.EMPTY);
    }
}
