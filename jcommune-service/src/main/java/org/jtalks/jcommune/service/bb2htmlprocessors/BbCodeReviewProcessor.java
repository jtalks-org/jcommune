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

import com.google.common.annotations.VisibleForTesting;
import org.jtalks.jcommune.service.TopicModificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.perm.kefir.bbcode.TextProcessor;
import ru.perm.kefir.bbcode.TextProcessorAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processor for bb2html which saves inner "[/code]" tags in code review posts. This is required because Code Reviews
 * initially have surrounding [code], but user himself also might have put there same string, without this processor all
 * this stuff is treated as bb codes and is replaced with HTML.<br/> Consider such situation: <ul> <li>User creates code
 * review which itself means that all the text is surrounded with [code]. This results in {@code
 * [code][-code][/code][/code]}</li> <li>We replace internal [/code] with [-code] inside of this pre-processor and place
 * that information inside of the {@link #REPLACE_HISTORY_LIST_ATTRIBUTE} attribute like this {@code false, true} which
 * means that internal [-code] is user input and not the work of our pre-processor [/code]</li> <li>In the {@link
 * #postProcess(String)} we find this attribute and we know what second [-code] should be replaced back to [/code]</li>
 * </ul>
 *
 * @author Evgeny Kapinos
 * @see <a href="http://jira.jtalks.org/browse/JC-1261">JIRA</a> for high-level details.
 */
public class BbCodeReviewProcessor extends TextProcessorAdapter implements TextProcessor, TextPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CODE_JAVA_BBCODE_END_REPLACEMENT = "[-code]";
    private static final String CODE_JAVA_BBCODE_END_REPLACEMENT_PATTERN = "\\[-code\\]";
    /**
     * This is an attribute in the http request which contains a list of bb-codes that should or should not be replaced
     * back.
     */
    @VisibleForTesting
    protected static final String REPLACE_HISTORY_LIST_ATTRIBUTE = "BBCodeReviewPreprocessor_replaceHistoryList";

    /**
     * Process incoming encoded text and replacing [/code] tags to [-code]
     *
     * @param bbEncodedText BB encoded text to process
     * @return processed text
     */
    @Override
    public String process(String bbEncodedText) {
        HttpServletRequest httpServletRequest = getServletRequest();
        httpServletRequest.removeAttribute(REPLACE_HISTORY_LIST_ATTRIBUTE);

        if (!isCodeReviewPost(httpServletRequest) || !isValidCodeReviewBbCodeString(bbEncodedText)) {
            return bbEncodedText;
        }

        List<Boolean> replaceHistoryList = getReplaceHistoryList(bbEncodedText);
        if (replaceHistoryList.isEmpty()) {
            return bbEncodedText;
        }

        httpServletRequest.setAttribute(REPLACE_HISTORY_LIST_ATTRIBUTE, replaceHistoryList);

        return substituteCloseCodeTagsWithTemporaryReplacementInEncodedText(bbEncodedText);
    }

    /**
     * Process incoming encoded text with replacing [/code] tags to [-code]
     *
     * @param bbEncodedText BB encoded text to process
     * @return processed text
     */
    @Override
    public CharSequence process(CharSequence bbEncodedText) {
        String result = process(bbEncodedText.toString());
        return new StringBuilder(result).subSequence(0, result.length());
    }

    /**
     * Process incoming decoded text by replacing [-code] tags to [/code].
     *
     * @param bbDecodedText text returned after to BBCode processor
     * @return resultant text
     */
    @Override
    public String postProcess(String bbDecodedText) {
        HttpServletRequest httpServletRequest = getServletRequest();

        if (!isCodeReviewPost(httpServletRequest)) {
            return bbDecodedText;
        }

        @SuppressWarnings("unchecked")
        List<Boolean> replaceHistoryList =
                (List<Boolean>) httpServletRequest.getAttribute(REPLACE_HISTORY_LIST_ATTRIBUTE);
        if (replaceHistoryList == null) {
            return bbDecodedText;
        }

        httpServletRequest.removeAttribute(REPLACE_HISTORY_LIST_ATTRIBUTE);

        return removeTemporaryReplacementSubstitutionFromDecodedText(bbDecodedText, replaceHistoryList);
    }

    /**
     * Gets current request
     *
     * @return native {@link HttpServletRequest}
     */
    @VisibleForTesting
    protected HttpServletRequest getServletRequest() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    /**
     * Checks in current request attributes Code Review token
     *
     * @return {@code true, false}
     */
    private boolean isCodeReviewPost(HttpServletRequest httpServletRequest) {
        String isCodeReviewPost = (String) httpServletRequest.getAttribute("isCodeReviewPost");
        return (isCodeReviewPost != null);
    }

    /**
     * Checks regular wrap [code]...[/code] in Code Review text
     *
     * @return {@code true, false}
     */
    private boolean isValidCodeReviewBbCodeString(String bbEncodedText) {
        // We use Pattern.DOTALL flag for extend ".*" pattern behavior to line terminators too 
        Pattern pattern = Pattern.compile(TopicModificationService.CODE_JAVA_BBCODE_START_PATTERN + ".*"
                + TopicModificationService.CODE_JAVA_BBCODE_END_PATTERN, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(bbEncodedText);
        if (!matcher.matches()) {
            logger.warn("BbCodeReviewProcessor called, but target encoded text \"" + bbEncodedText
                    + "\" doesn't wrapped with " + TopicModificationService.CODE_JAVA_BBCODE_START + "..."
                    + TopicModificationService.CODE_JAVA_BBCODE_END
                    + " BBCodes. Check \"isCodeReviewPost\" request attribute");
            return false;
        }
        return true;
    }

    /**
     * Removes regular wrap [code]...[/code] in Code Review text
     *
     * @return user text from full Code Review with wrap
     */
    private String getUserCodeReviewText(String bbEncodedText) {
        return bbEncodedText.substring(TopicModificationService.CODE_JAVA_BBCODE_START.length(),
                bbEncodedText.length() -
                        TopicModificationService.CODE_JAVA_BBCODE_END.length());
    }

    /**
     * Analyze Code Review text and creates replace history list
     *
     * @return replace history list
     */
    private List<Boolean> getReplaceHistoryList(String bbEncodedText) {
        String textOnly = getUserCodeReviewText(bbEncodedText);

        List<Boolean> replaceHistoryList = new ArrayList<Boolean>();

        Pattern pattern = Pattern.compile(TopicModificationService.CODE_JAVA_BBCODE_END_PATTERN + "|"
                + CODE_JAVA_BBCODE_END_REPLACEMENT_PATTERN);
        Matcher matcher = pattern.matcher(textOnly);
        while (matcher.find()) {
            replaceHistoryList.add(matcher.group().equals(TopicModificationService.CODE_JAVA_BBCODE_END));
        }
        return replaceHistoryList;
    }

    /**
     * Creates new BBcode string without [/code] tags in user part (replaced by [-code] tag)
     *
     * @return safety BBcode string with temporary replacements
     */
    private String substituteCloseCodeTagsWithTemporaryReplacementInEncodedText(String bbEncodedText) {
        return TopicModificationService.CODE_JAVA_BBCODE_START
                + getUserCodeReviewText(bbEncodedText).replaceAll(TopicModificationService.CODE_JAVA_BBCODE_END_PATTERN,
                CODE_JAVA_BBCODE_END_REPLACEMENT)
                + TopicModificationService.CODE_JAVA_BBCODE_END;
    }

    /**
     * Returns back original tags into already processed string. To be precise: [-code] tags are replaced back with
     * [code] tags
     *
     * @param bbDecodedText      the text after it was processed into HTML, it yet contains [-code] tags
     * @param replaceHistoryList true in the list means that [-code] should be replaced with [/code], false would mean
     *                           that [-code] is the original text written by user and it shouldn't be replaced with
     *                           [/code]
     * @return BBcode string without temporary replacements
     */
    private String removeTemporaryReplacementSubstitutionFromDecodedText(String bbDecodedText,
                                                                         List<Boolean> replaceHistoryList) {
        int index = 0;
        Pattern pattern = Pattern.compile(CODE_JAVA_BBCODE_END_REPLACEMENT_PATTERN);
        Matcher matcher = pattern.matcher(bbDecodedText);
        StringBuffer sb = new StringBuffer();
        try {
            while (matcher.find()) {
                if (replaceHistoryList.get(index)) {
                    matcher.appendReplacement(sb, TopicModificationService.CODE_JAVA_BBCODE_END);
                }
                index++;
            }
            matcher.appendTail(sb);
            return sb.toString();
        } catch (IndexOutOfBoundsException e) {
            logger.warn("BbCodeReviewProcessor called, but target decoded text \"" + bbDecodedText
                    + "\" doesn't contain " + replaceHistoryList.size() + " expected temporary replacement elements "
                    + CODE_JAVA_BBCODE_END_REPLACEMENT);
            return bbDecodedText;
        }
    }
}
