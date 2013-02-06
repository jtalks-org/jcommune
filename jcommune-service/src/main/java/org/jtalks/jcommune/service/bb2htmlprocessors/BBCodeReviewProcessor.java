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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jtalks.jcommune.service.TopicModificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.annotations.VisibleForTesting;

import ru.perm.kefir.bbcode.TextProcessor;
import ru.perm.kefir.bbcode.TextProcessorAdapter;

/**
 * Processor for bb2html which save inner "[/code]" tags in code review posts  
 * @author Evgeny Kapinos
 *
 */
public class BBCodeReviewProcessor extends TextProcessorAdapter implements TextProcessor, TextPostProcessor  {

    private final static Logger logger = LoggerFactory.getLogger(BBCodeReviewProcessor.class);

    private static final String CODE_JAVA_BBCODE_END_REPLACEMENT = "[-code]";
    private static final String CODE_JAVA_BBCODE_END_REPLACEMENT_PATTERN = "\\[-code\\]";
    @VisibleForTesting
    protected static final String RHL_ATTRIBUTE = "BBCodeReviewPreprocessor_replaceHistoryList";

    /**
     * Process incoming encoded text with replacing [/code] tags to [-code]
     * @param bbEncodedText BB encoded text to process
     * @return processed text 
     */
    @Override
    public String process(String bbEncodedText) {
        HttpServletRequest httpServletRequest = getServletRequest();
        httpServletRequest.removeAttribute(RHL_ATTRIBUTE);
        
        String isCodeReviewPost = (String) httpServletRequest.getAttribute("isCodeReviewPost");      
        if (isCodeReviewPost == null){
            return bbEncodedText;
        }
        
        if (!bbEncodedText.matches(TopicModificationService.CODE_JAVA_BBCODE_START_PATTERN + ".*" 
                                 + TopicModificationService.CODE_JAVA_BBCODE_END_PATTERN)){
            logger.warn("BBCodeReviewProcessor called, but target encoded text \"" + bbEncodedText  
                      + "\" doesn't wrapped with " + TopicModificationService.CODE_JAVA_BBCODE_START + "..." 
                      + TopicModificationService.CODE_JAVA_BBCODE_END
                      + " BBCodes. Check \"isCodeReviewPost\" request attribute");
            return bbEncodedText;
        }
        
        String textOnly = bbEncodedText.substring(TopicModificationService.CODE_JAVA_BBCODE_START.length(), 
                                                  bbEncodedText.length() - 
                                                  TopicModificationService.CODE_JAVA_BBCODE_END.length());
        
        List<Boolean> replaceHistoryList = new ArrayList<Boolean>();
        
        Pattern pattern = Pattern.compile(TopicModificationService.CODE_JAVA_BBCODE_END_PATTERN + "|" 
                                        + CODE_JAVA_BBCODE_END_REPLACEMENT_PATTERN);
        Matcher matcher = pattern.matcher(textOnly);
        while (matcher.find()){
            replaceHistoryList.add(matcher.group().equals(TopicModificationService.CODE_JAVA_BBCODE_END));
        }
       
        if (replaceHistoryList.isEmpty()){
            return bbEncodedText;
        }

        httpServletRequest.setAttribute(RHL_ATTRIBUTE, replaceHistoryList);
        return TopicModificationService.CODE_JAVA_BBCODE_START 
             + textOnly.replaceAll(TopicModificationService.CODE_JAVA_BBCODE_END_PATTERN, 
                                   CODE_JAVA_BBCODE_END_REPLACEMENT)
             + TopicModificationService.CODE_JAVA_BBCODE_END;
    }
    
    /**
     * Process incoming encoded text with replacing [/code] tags to [-code]
     * @param bbEncodedText BB encoded text to process
     * @return processed text
     */
    @Override
    public CharSequence process(CharSequence bbEncodedText) {
        String result = process(new StringBuilder(bbEncodedText).toString()); 
        return new StringBuilder(result).subSequence(0, result.length());
    }

    /**
     * Process incoming decoded text with replacing [-code] tags to [/code]
     * @param bbEncodedText BB encoded text to process
     * @return processed text
     */
    @Override
    public String postProcess(String source) {
        HttpServletRequest httpServletRequest = getServletRequest();
        
        String isCodeReviewPost = (String) httpServletRequest.getAttribute("isCodeReviewPost");
        if (isCodeReviewPost == null){
            return source;
        }
        
        @SuppressWarnings("unchecked")
        List<Boolean> replaceHistoryList = (List<Boolean>) httpServletRequest.getAttribute(RHL_ATTRIBUTE);
        if (replaceHistoryList == null){
            return source;
        }
        httpServletRequest.removeAttribute(RHL_ATTRIBUTE);
        
        int index = 0;
        Pattern pattern = Pattern.compile(CODE_JAVA_BBCODE_END_REPLACEMENT_PATTERN);
        Matcher matcher = pattern.matcher(source);
        StringBuffer sb = new StringBuffer();
        try{
            while (matcher.find()){
                if (replaceHistoryList.get(index)){
                    matcher.appendReplacement(sb, TopicModificationService.CODE_JAVA_BBCODE_END);
                }          
                index++;
            }
            matcher.appendTail(sb);
            return sb.toString();
        } catch (IndexOutOfBoundsException e){
            logger.warn("BBCodeReviewProcessor called, but target decoded text \"" + source  
                    + "\" doesn't contain "+replaceHistoryList.size()+" expected temporary replacment elements "
                    + CODE_JAVA_BBCODE_END_REPLACEMENT);
            return source;
        }
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
}
