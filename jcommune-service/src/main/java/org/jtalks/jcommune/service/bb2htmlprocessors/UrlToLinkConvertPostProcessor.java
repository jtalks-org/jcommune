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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This text postprocessor searches URLs in text and wrap them in &lt;a&gt tag to highlight.
 * Links which are already inside &lt;a&gt or &lt;img&gt tag are skipped.
 * Created by Alexey Usharovskiy on 25.12.16.
 */
public class UrlToLinkConvertPostProcessor implements TextPostProcessor {

    private static final Pattern htmlTagsToSkip = Pattern.compile(
            "(<a.*?>.*?</a>|<img.*?>.*?</img>|<pre.*?>.*?</pre>)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern urlInText = Pattern.compile(
            "(\\b((?:https?|ftp|file):\\/\\/|www\\.|ftp\\.)[-A-Z0-9+&@#\\/%?=~_|!:,.;]*[-A-Z0-9+&@#\\/%=~_|])",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final String htmlLinkTemplate = "<a href=\"%s\">%s</a>";

    @Override
    public String postProcess(String bbDecodedText) {
        return processLinks(bbDecodedText);
    }

    /**
     * Search for text blocks outside &lt;a&gt and &lt;img&gt tags,
     * pass them to URL process method and build resulting post text
     * with highlighted URLs
     *
     * @param bbEncodedText bb encoded text to process
     * @return processed text
     */
    private static String processLinks(String bbEncodedText) {
        StringBuilder stringBuilder = new StringBuilder();
        int prevPos = 0;
        Matcher matcher = htmlTagsToSkip.matcher(bbEncodedText);
        while (matcher.find()) {
            String substring = bbEncodedText.substring(prevPos, matcher.start());
            if (!substring.isEmpty()) {
                stringBuilder.append(processUrlInText(substring));
            }
            stringBuilder.append(matcher.group());
            prevPos = matcher.end();
        }
        if (prevPos == 0) {
            return processUrlInText(bbEncodedText);
        }
        String substring = bbEncodedText.substring(prevPos, bbEncodedText.length());
        if (!substring.isEmpty()) {
            stringBuilder.append(processUrlInText(substring));
        }
        return stringBuilder.toString();
    }

    /**
     * Search by URLs in textBlock and wrap them inside tags to highlight
     *
     * @param textBlock text for URL search and wrap
     * @return text with wrapped URLs
     */
    private static String processUrlInText(String textBlock) {
        Matcher matcher = urlInText.matcher(textBlock);
        StringBuilder stringBuilder = new StringBuilder();
        int prevPos = 0;
        while (matcher.find()) {
            stringBuilder.append(textBlock.substring(prevPos, matcher.start()));
            stringBuilder.append(complementUrlWithProtocol(matcher.group(1), matcher.group(2)));
            prevPos = matcher.end();
        }
        if (prevPos == 0) {
            return textBlock;
        }
        String substring = textBlock.substring(prevPos, textBlock.length());
        if (!substring.isEmpty()) {
            stringBuilder.append(substring);
        }
        return stringBuilder.toString();
    }

    /**
     * Complement URL with protocol if URL begin from www. or ftp.
     *
     * @param url URL text
     * @param protocol protocol from URL
     * @return URL which is completed with protocol if needs
     */
    private static String complementUrlWithProtocol(String url, String protocol) {
        String htmlLink;
        switch (protocol) {
            case "www.":
                htmlLink = "http://" + url;
                break;
            case "ftp.":
                htmlLink = "ftp://" + url;
                break;
            default:
                htmlLink = url;
        }
        return String.format(htmlLinkTemplate, htmlLink, url);
    }
}
