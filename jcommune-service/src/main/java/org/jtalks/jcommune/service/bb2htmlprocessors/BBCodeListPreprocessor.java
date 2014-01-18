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
package org.jtalks.jcommune.service.bb2htmlprocessors;

import org.kefirsf.bb.TextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Preprocessor for bb2html encoding which replaces all list items like [*] with [*]...[/*] tags. This allows create
 * formatted text in list items.
 *
 * @author Vyacheslav Mishcheryakov
 */
public class BBCodeListPreprocessor implements TextProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     *
     * @param bbEncodedText BB encoded text to process
     * @return processed text
     */
    @Override
    public String process(String bbEncodedText) {
        return process(new StringBuilder(bbEncodedText)).toString();
    }

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     *
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    private StringBuilder preprocessLists(String bbEncodedText) {
        try {
            ListItemsProcessor processor = new ListItemsProcessor(bbEncodedText);
            return processor.getTextWithClosedTags();
        } catch (BBCodeListParsingException lpe) {
            logger.info("Ignored invalid [list] tag:" + bbEncodedText);
            return new StringBuilder(bbEncodedText);
        }
    }

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     *
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    @Override
    public CharSequence process(CharSequence bbEncodedText) {
        StringBuilder result = process(new StringBuilder(bbEncodedText));
        return result.subSequence(0, result.length());
    }

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     *
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    @Override
    public StringBuilder process(StringBuilder bbEncodedText) {
        return preprocessLists(bbEncodedText.toString());
    }

    /**
     * Process incoming text with replacing [*] tags by [*]...[/*]
     *
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    @Override
    public StringBuffer process(StringBuffer bbEncodedText) {
        return new StringBuffer(process(bbEncodedText.toString()));
    }
}