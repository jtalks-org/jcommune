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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.bb2htmlprocessors.TextPostProcessor;

import ru.perm.kefir.bbcode.BBProcessorFactory;
import ru.perm.kefir.bbcode.TextProcessor;

/**
 * Provides various helper methods for encoding/decoding BB codes
 *
 * @author Evgeniy Naumenko
 */
public class BBCodeService {

    private static final String QUOTE_PATEERN = "[quote=\"%s\"]%s[/quote]";

    /**
     * Processor is thread safe as it's explicitly stated in documentation
     */
    private TextProcessor processor = BBProcessorFactory.getInstance().create();
    
    /**
     * Preprocessors of BB encoded text used before actual BB2HTML converter
     */
    private List<TextProcessor> preprocessors = new ArrayList<TextProcessor>();
    
    /**
     * Postprocessors of BB encoded text used after actual BB2HTML converter
     */
    private List<TextPostProcessor> postprocessors = new ArrayList<TextPostProcessor>();
    
    /**
     * Quotes text given as a valid BB-coded quote.
     * Such a quotes are rendered automatically in posts or forum messages
     *
     * @param source text to quote, not null
     * @param author text author, not null
     * @return well formed BB qoute
     */
    public String quote(String source, JCUser author) {
        Validate.notNull(source, "Source cannot be null");
        Validate.notNull(author, "Author cannot be null");
        return String.format(QUOTE_PATEERN, author.getUsername(), source);
    }

    /**
     * Converts BB-encoded text into HTML-encoded one. Actual transformation result
     * depend on kefirBB.xml configuration and the CSS styles mentioned in it's patterns.
     * <p/>
     * If input text contains no BB-conpatible tags it's returned as is.
     *
     * @param bbEncodedText string with BB-style markup
     * @return the same text with HTML markup to be shown
     */
    public String convertBbToHtml(String bbEncodedText) {
        for (TextProcessor preprocessor : preprocessors) {
            bbEncodedText = preprocessor.process(bbEncodedText);;
        }
        bbEncodedText = processor.process(bbEncodedText);
        for (TextPostProcessor postpreprocessor : postprocessors) {
            bbEncodedText = postpreprocessor.postProcess(bbEncodedText);;
        }
        return bbEncodedText;
    }

    /**
     * Removes all BB codes from the text given, simply cutting
     * out all [...]-style tags found
     *
     * @param source text to cleanup
     * @return plain text without BB tags
     */
    public String removeBBCodes(String source) {
        return source.replaceAll("\\[.*?\\]", "");
    }

    /**
     * @return the preprocessors
     */
    public List<TextProcessor> getPreprocessors() {
        return preprocessors;
    }

    /**
     * @param preprocessors the preprocessors to set
     */
    public void setPreprocessors(List<TextProcessor> preprocessors) {
        this.preprocessors = preprocessors;
    }
    
    /**
     * @return the preprocessors
     */
    public List<TextPostProcessor> getPostprocessors() {
        return postprocessors;
    }

    /**
     * @param preprocessors the preprocessors to set
     */
    public void setPostprocessors(List<TextPostProcessor> postprocessors) {
        this.postprocessors = postprocessors;
    }
    
}
