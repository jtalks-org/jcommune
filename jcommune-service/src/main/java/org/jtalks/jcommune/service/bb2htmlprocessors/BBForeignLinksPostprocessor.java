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
 * <p/>
 * Note: Nofollow is an attribute that can be added to links to discourage Comment Spam. It is used with the rel=" "
 * attribute in a link. By default, posting links generates no positive benefit for the poster in terms of PageRank
 * (or other search engine value) the spammers will be dissuaded from wasting their time.
 *
 * @author Andrey Pogorelov
 * @see <a href="http://jira.jtalks.org/browse/JC-1421">JIRA</a> for more details.
 */
public class BBForeignLinksPostprocessor implements TextPostProcessor {

    private static final String URL_PATTERN = "(<a .*?href=(\"|').*?(\"|')|<img .*?src=(\"|').*?(\"|'))";

    /**
     * Process incoming text with adding prefix "/out" to foreign links. This prefix
     * will be excluded from indexing by search engines (robots.txt)
     *
     * @return resultant text
     */
    @Override
    public String postProcess(String bbDecodedText) {
        HttpServletRequest httpServletRequest = getServletRequest();
        return addPrefixToForeignLinks(bbDecodedText, httpServletRequest.getServerName());
    }


    private String addPrefixToForeignLinks(String decodedText, String serverName) {
        Pattern linkPattern = Pattern.compile(URL_PATTERN, Pattern.DOTALL);
        Matcher linkMatcher = linkPattern.matcher(decodedText);
        String href;
        String encoded;
        while (linkMatcher.find()) {
            href = linkMatcher.group();
            encoded = href.replaceAll(" ", "%20");
            if (!href.contains(serverName) && href.split("(http|ftp|https)://", 2).length == 2
                    && href.startsWith("<a")) {
                decodedText = decodedText.replace(href,
                        encoded.replaceFirst("<a.*href=\"", "<a rel=\"nofollow\" href=\"" + getHrefPrefix()));
            } else if(href.startsWith("<a")){
                decodedText = decodedText.replace(href,
                        encoded.replaceFirst("<a.*href=\"", "<a href=\""));
            } else if(href.startsWith("<img")) {
                decodedText = decodedText.replace(href,
                        encoded.replaceFirst("<img.*src=\"", "<img class=\"thumbnail\" src=\""));
            }
        }

        return decodedText;
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

    /**
     * Gets prefix to add href
     *
     * @return prefix
     */
    @VisibleForTesting
    protected String getHrefPrefix() {
        return "/out?url=";
    }

}
