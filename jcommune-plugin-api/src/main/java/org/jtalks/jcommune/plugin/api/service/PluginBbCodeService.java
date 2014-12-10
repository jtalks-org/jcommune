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
package org.jtalks.jcommune.plugin.api.service;

/**
 * @author Mikhail Stryzhonok
 */
public interface PluginBbCodeService {

    /**
     * Remove bb-codes from the specified string.
     * It remove ONLY VALID bb-codes. So, something like [zzz][/zzz] is unchanged.
     * Also it doesn't strip open bb-code when there are no appropriate close tag.
     * @param bbCode text with bb-codes
     * @return text without bb-codes
     */
    String stripBBCodes(String bbCode);

    /**
     * <p>Converts BB-encoded text into HTML-encoded one. Actual transformation result depends on kefirBB.xml
     * configuration and the CSS styles mentioned in it's patterns. Uses pre-processors to do some work on input text,
     * like closing un-closed tags. Also it uses post-processors e.g. for code reviews to return back [code] tag after
     * it was removed in the pre-processors.</p> If input text contains <i>no</i> BB-compatible tags it's returned as
     * is.
     *
     * @param bbEncodedText string with BB-style markup
     * @return the same text with HTML markup to be shown
     */
    String convertBbToHtml(String bbEncodedText);
}
