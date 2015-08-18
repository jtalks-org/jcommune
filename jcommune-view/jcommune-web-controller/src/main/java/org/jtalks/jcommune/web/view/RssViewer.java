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

package org.jtalks.jcommune.web.view;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import org.jtalks.common.model.entity.Component;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class forms a RSS feed view.
 *
 * @author Andrey Kluev
 */
public class RssViewer extends AbstractRssFeedView {

    private static final String DEFAULT_FEED_TITLE = "";
    private static final String DEFAULT_FEED_DESCRIPTION = "";

    /**
     * Set meta data for all RSS feed
     *
     * @param model   news model
     * @param feed    news feed
     * @param request http request
     */
    @Override
    protected void buildFeedMetadata(Map<String, Object> model, Channel feed,
                                     HttpServletRequest request) {
        Component component = (Component)model.get("forumComponent");
        String feedTitle = DEFAULT_FEED_TITLE;
        String feedDescription = DEFAULT_FEED_DESCRIPTION;

        if (component != null) {
            feedTitle = component.getName();
            feedDescription = component.getDescription();
        }

        feed.setTitle(feedTitle);
        feed.setDescription(feedDescription);
        feed.setLink(buildURL(request));

        super.buildFeedMetadata(model, feed, request);
    }

    /**
     * Set list data item news in RSS feed
     *
     * @param model    news model
     * @param request  http request
     * @param response http response
     * @return list items
     * @throws IOException i/o exception
     */
    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model,
                                        HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        String url = buildURL(request);
        List<Topic> listContent = (List<Topic>) model.get("topics");
        if (listContent == null) {
            response.sendRedirect(request.getContextPath() + "/errors/404");
            return null;
        }
        List<Item> items = new ArrayList<>(listContent.size());

        for (Topic topic : listContent) {
            items.add(createFeedItem(topic, url));
        }

        response.setContentType(getContentType());
        return items;
    }

    /**
     * Create news item
     *
     * @param topic   news topic
     * @param url building URL
     * @return item for news feed
     */
    private Item createFeedItem(Topic topic, String url) {

        Item item = new Item();
        Description description = new Description();
        description.setType("text");
        description.setValue(topic.getLastPost().getPostContent());

        Content content = new Content();
        item.setContent(content);

        item.setTitle(topic.getTitle());
        item.setAuthor(topic.getLastPost().getUserCreated().getUsername());

        item.setLink(url + "/posts/" + topic.getLastPost().getId());

        item.setComments(topic.getTopicStarter().getSignature());
        item.setDescription(description);
        item.setPubDate(topic.getModificationDate().toDate());
        return item;
    }

    /**
     * The implementation of building url
     *
     * @param request HttpServletRequest
     * @return url
     */
    private String buildURL(HttpServletRequest request) {
        return request.getScheme()
                + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }

}