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

/**
 * Holds a list of the languages available
 */
public enum Language {

    ENGLISH {
        /**
         * Return resource bundle label for the English language. This method should be used when
         * you need a localized representation of the current instance
         *
         * @return string resource bundle label
         */
        @Override
        public String getLanguageNameLabel() {
            return "label.english";
        }

        /**
         * @return language abbreviation used in locale settings
         */
        @Override
        public String getLanguageCode() {
            return "en";
        }
    },

    RUSSIAN {
        /**
         * Return resource bundle label for the Russian language. This method should be used when
         * you need a localized representation of the current instance
         *
         * @return string resource bundle label
         */
        @Override
        public String getLanguageNameLabel() {
            return "label.russian";
        }

        /**
         * @return language abbreviation used in locale settings
         */
        @Override
        public String getLanguageCode() {
            return "ru";
        }
    };

    /**
     * Return resource bundle label for the language name. This method should be used when
     * you need a localized representation of the current instance
     *
     * @return string resource bundle label
     */
    public abstract String getLanguageNameLabel();

    /**
     * @return language abbreviation used in locale settings, like "en" or "ru"
     */
    public abstract String getLanguageCode();
}
