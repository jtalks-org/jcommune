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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PostProcessor for bb2html which adds attribute rel="nofollow" to foreign links. It's done in order to keep our Google
 * Rating higher.
 * <p>
 * Note: Nofollow is an attribute that can be added to links to discourage Comment Spam. It is used with the rel=" "
 * attribute in a link. By default, posting links generates no positive benefit for the poster in terms of PageRank
 * (or other search engine value) the spammers will be dissuaded from wasting their time.
 *
 * @author Andrey Pogorelov
 * @see <a href="http://jira.jtalks.org/browse/JC-1421">JIRA</a> for more details.
 */
public class BBForeignLinksPostprocessor implements TextPostProcessor {

    private static final String LINK_PATTERN = "<a .*?href=(\"|').*?(\"|')";

    private static final String URL_PATTERN = "<a .*?href=(\"|')(((http|ftp|https)://)?" +
            "([\\w\\-_]+(\\.[\\w\\-_]+)+|localhost)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?)(\"|')";

     /**
     * Process incoming text with adding attribute rel="nofollow" to foreign links
     *
     * @param bbDecodedText text returned after to BBCode processor
     * @return resultant text
     */
    @Override
    public String postProcess(String bbDecodedText) {
        HttpServletRequest httpServletRequest = getServletRequest();
        return addNofollowToForeignLinks(bbDecodedText, httpServletRequest.getServerName());
    }

    private String addNofollowToForeignLinks(String decodedText, String serverName){
        Pattern linkPattern = Pattern.compile(LINK_PATTERN, Pattern.DOTALL);
        Matcher linkMatcher = linkPattern.matcher(decodedText);
        Pattern pattern = Pattern.compile(URL_PATTERN, Pattern.DOTALL);
        Matcher matcher;
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (linkMatcher.find())
        {
            matcher = pattern.matcher(linkMatcher.group());
            result.append(decodedText.substring(lastEnd, linkMatcher.start()));
            if(matcher.matches() && matcher.group(5).toLowerCase().endsWith(serverName.toLowerCase())){
                result.append(linkMatcher.group());
            } else {
                String replacement = String.format("%s rel=\"nofollow\"", linkMatcher.group());
                result.append(replacement);
            }

            lastEnd = linkMatcher.end();
        }

        result.append(decodedText.substring(lastEnd, decodedText.length()));
        return result.toString();
    }

    /**
     * Gets current request
     *
     * @return native {@link javax.servlet.http.HttpServletRequest}
     */
    @VisibleForTesting
    protected HttpServletRequest getServletRequest() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

}
