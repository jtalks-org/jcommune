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

import java.util.Locale;

/**
 * Holds a list of the languages available
 */
public enum Language {

    //simple alphabetical order, no holywars please
    ENGLISH("label.english", "en"),
    RUSSIAN("label.russian", "ru"),    
    SPANISH("label.spanish", "es"),
    UKRAINIAN("label.ukrainian", "uk");

    private String label;
    private String code;
    private Locale locale;

    /**
     * @param label same as in resource bundle
     * @param code  locale code for this language
     */
    private Language(String label, String code) {
        this.label = label;
        this.code = code;
        this.locale = new Locale(code);
    }

    /**
     * @param locale locale to select language constant for
     * @return Language constant matches the locale given. If nothing matches exception is to be raised
     */
    public static Language byLocale(Locale locale){
        for (Language language : values()){
            if (language.locale.equals(locale)){
                return language;
            }
        }
        throw new IllegalArgumentException("Locale does not match any known language");
    }

    /**
     * Returns locale accordingly language code.
     *
     * @return Locale object
     */
    public Locale getLocale(){
        return locale;
    }
    
    /**
     * Return resource bundle label for the language name. This method should be used when
     * you need a localized representation of the current instance
     *
     * @return string resource bundle label
     */
    public String getLanguageNameLabel() {
        return label;
    }

    /**
     * @return language abbreviation used in locale settings, like "en" or "ru"
     */
    public String getLanguageCode() {
        return code;
    }
}
