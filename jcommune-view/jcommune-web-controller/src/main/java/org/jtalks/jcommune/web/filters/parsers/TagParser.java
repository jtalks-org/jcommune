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
package org.jtalks.jcommune.web.filters.parsers;

import org.jtalks.jcommune.web.filters.wrapper.TaggedResponseWrapper;

import java.io.UnsupportedEncodingException;

/**
 * Interface which should be implemented by tag parsers
 *
 * @author Mikhail Stryzhonok
 */
public interface TagParser {

    /**
     * Replaces tag by appropriate content. We need tags to access to application resources (e.g. i18n messages)
     * from velocity macros provided by plugin-api module and from plugins templates itself (if necessary).
     *
     * @param response response to search tags
     * @return array of bytes which represents response content
     * @throws java.io.UnsupportedEncodingException if response have unsupported encoding
     */
    byte[] replaceTagByContent(TaggedResponseWrapper response) throws UnsupportedEncodingException;
}
