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

import org.apache.commons.lang.Validate;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.service.bb2htmlprocessors.TextPostProcessor;
import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides various helper methods for encoding/decoding BB codes. This class is used by our JSTL tags on JSP pages.
 *
 * @author Evgeniy Naumenko
 */
public class BBCodeService {
    private static final String QUOTE_PATEERN = "[quote=\"%s\"]%s[/quote]";
    /** Processor is thread safe as it's explicitly stated in documentation */
    private final TextProcessor processor = BBProcessorFactory.getInstance().create();
    /** Processor to strip bb-codes */
    private final TextProcessor stripBBCodesProcessor = BBProcessorFactory.getInstance().createFromResource("kefirbb-strip-config.xml");
    /** Preprocessors of BB encoded text used before actual BB2HTML converter */
    private final List<TextProcessor> preprocessors = new ArrayList<TextProcessor>();

    /**
     * Postprocessors of BB decoded text used after actual BB2HTML converter. This is needed for instance in case of
     * Code Reviews when we first change user's input to get rid of extra bb-codes, and then put them back after bb
     * codes has been processed.
     */
    private final List<TextPostProcessor> postprocessors = new ArrayList<TextPostProcessor>();

    /**
     * Quotes text given as a valid BB-coded quote. Such a quotes are rendered automatically in posts or forum messages.
     * On UI you can find Quote button near each post.
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
     * <p>Converts BB-encoded text into HTML-encoded one. Actual transformation result depends on kefirBB.xml
     * configuration and the CSS styles mentioned in it's patterns. Uses pre-processors to do some work on input text,
     * like closing un-closed tags. Also it uses post-processors e.g. for code reviews to return back [code] tag after
     * it was removed in the pre-processors.</p> If input text contains <i>no</i> BB-compatible tags it's returned as
     * is.
     *
     * @param bbEncodedText string with BB-style markup
     * @return the same text with HTML markup to be shown
     */
    public String convertBbToHtml(String bbEncodedText) {
        for (TextProcessor preprocessor : preprocessors) {
            bbEncodedText = preprocessor.process(bbEncodedText);
        }
        bbEncodedText = processor.process(bbEncodedText);
        for (TextPostProcessor postpreprocessor : postprocessors) {
            bbEncodedText = postpreprocessor.postProcess(bbEncodedText);
        }
        return bbEncodedText;
    }

    /** @param preprocessors objects that process input text from users post before the actual bb-converting is
     *                       started */
    public void setPreprocessors(List<TextProcessor> preprocessors) {
        this.preprocessors.addAll(preprocessors);
    }

    /**
     * @param postprocessors objects that process input text from users' posts after it was processed by {@link
     *                       TextProcessor}, this is useful for instance for code reviews when we need to do some
     *                       clean-up work.
     */
    public void setPostprocessors(List<TextPostProcessor> postprocessors) {
        this.postprocessors.addAll(postprocessors);
    }

    /**
     * Remove bb-codes from the specified string.
     * It remove ONLY VALID bb-codes. So, something like [zzz][/zzz] is unchanged.
     * Also it doesn't strip open bb-code when there are no appropriate close tag.
     * @param bbCode text with bb-codes
     * @return text without bb-codes
     */
    public String stripBBCodes(String bbCode) {
        return stripBBCodesProcessor.process(bbCode);
    }
}
