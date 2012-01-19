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
package org.jtalks.jcommune.service.nontransactional;

import org.jtalks.jcommune.model.entity.JCUser;

/**
 * Provides various helper methods for encoding/decoding BB codes
 *
 * @author Evgeniy Naumenko
 */
public class BBCodeService {

    private static final String QUOTE_PATEERN = "[quote=\"%s\"]%s[/quote]";

    /**
     * Qoutes text given as a valid BB-coded quote.
     * Sucha  quotes are rendered automatically in posts or forum messages
     *
     * @param source text to quote, not null
     * @param author text author, not null
     * @return well formed BB qoute
     */
    public String quote(String source, JCUser author) {
        if (source == null || author == null) {
            throw new IllegalArgumentException("Author and source cannot be null");
        }
        return String.format(QUOTE_PATEERN, author.getUsername(), source);
    }
}
