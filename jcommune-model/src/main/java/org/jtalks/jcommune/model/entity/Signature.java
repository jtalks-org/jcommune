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
package org.jtalks.jcommune.model.entity;

import org.springframework.web.util.HtmlUtils;

import java.io.Serializable;

/**
 * Represents user signature and encapsulates it's rendering.
 * This class is transient, actual signature representation is
 *
 * @author Evgeniy Naumenko
 */
public class Signature implements Serializable {

    public static final String RENDERING_TEMPLATE = "<hr/><span class='signature'>%s</span>";
    private String content;
    private static final String HYPERLINK_REGEX =
            "([^\\s]*)(https?|ftp|file)(://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])";
    private static final String HYPERLINK_TEMPLATE = "<a href=\"" + "$2$3" + "\">" + "$1$2$3" + "</a>";


    /**
     * Creates new immutable instance.
     * Once initialized it's contents cannot be changed.
     *
     * @param content raw signature entered by the user
     */
    public Signature(String content) {
        this.content = content;
    }

    /**
     * Returns raw signature content without any
     * escaping or formatting applied
     *
     * @return bare user signature, exactly as it was entered
     */
    public String getContent() {
        return content;
    }

    /**
     * Renders user signature to be displayed as html content. This method also performs html escaping
     * so no injection/XSS attack is possible. Hyperlinks are searched using regular expressions and
     * replaced with html tags according to hyperlink template
     *
     * @return rendered user signature
     */
    public String render() {
        if (content == null) {
            return "";
        } else {
            String escaped = HtmlUtils.htmlEscape(content);
            content = escaped.replaceAll(HYPERLINK_REGEX, HYPERLINK_TEMPLATE);

        }
        return String.format(RENDERING_TEMPLATE, content);
    }
}
