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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds and replaces message code by message content.
 * Needed to give possibility to use jcommune i18n messages in plugin api and plugins itself.
 *
 * @author Mikhail Stryzhonok
 */
public class MessageTagParser implements TagParser {
    /**
     * Pattern for tag. EXAMPLE : <jcommune:message locale="en">message.code</jcommune:message>
     */
    private static final Pattern MESSAGE_TAG_PATTERN = Pattern.compile("<\\s?jcommune:message\\s*locale\\s?=\\s?[\"'](.*?)[\"']\\s?>(.*?)<\\s?/\\s?jcommune:message\\s?>",
            Pattern.CASE_INSENSITIVE);
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean replaceTagByContent(StringBuffer response) {
        boolean result = false;
        Matcher matcher = MESSAGE_TAG_PATTERN.matcher(response);
        while (matcher.find()) {
            if (matcher.groupCount() == 2) {
                result = true;
                Locale locale = Locale.forLanguageTag(matcher.group(1).trim());
                String key = matcher.group(2).trim();
                response.replace(matcher.start(), matcher.end(), getStringByKey(key, locale));
                matcher.reset();
            }
        }
        return result;
    }

    protected String getStringByKey(String key, Locale locale) {
        ResourceBundle bundle = getBundle(locale);
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            bundle = getBundle(DEFAULT_LOCALE);
            return bundle.containsKey(key) ? bundle.getString(key) : key;
        }
    }

    /**
     * Needed for mocking
     */
    protected ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle("org.jtalks.jcommune.web.view.messages", locale);
    }
}
