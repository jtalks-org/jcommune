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

import javax.servlet.http.HttpServletRequest;

/**
 * Holds a list of the language available
 */
public enum Language {

    ENGLISH {
        /**
         * @inheritDoc
         */
        @Override
        public String getLanguageNameLabel() {
            return "label.english";
        }

        /**
         * @inheritDoc
         */
        @Override
        public String getLanguageCode() {
            return "en";
        }
    },

    RUSSIAN {
        /**
         * @inheritDoc
         */
        @Override
        public String getLanguageNameLabel() {
            return "label.russian";
        }

        /**
         * @inheritDoc
         */
        @Override
        public String getLanguageCode() {
            return "ru";
        }
    };

    /**
     * Return resource bundle label for the languge name. This method should be used when
     * you need a localized representation of the current instance
     *
     * @return string resource bundle label
     */
    public abstract String getLanguageNameLabel();

    /**
     * @return language abbreviation used in locale settings, like "en" or "ru"
     */
    public abstract String getLanguageCode();

    /**
     * Builds the same link as in servlet request passed but with
     * language parameter added. If the request already have
     * language defined it should be replaced with the current
     * Language instance code
     *
     * @param request servlet request to gather link information from
     * @return link to the current page with params
     */
    public String buildLink(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder(request.getRequestURL());
        builder.append("?");
        String query = request.getQueryString();
        if (query == null) {    // no params in URL
            builder.append("lang=");
        } else {
            if (query.contains("lang=")) {
                // supposing the language is always at the end of the query string
                builder.append(query.substring(0, query.length() - 2));
            } else {
                builder.append(query);
                builder.append("&lang=");
            }
        }
        builder.append(this.getLanguageCode());
        return builder.toString();
    }
}
