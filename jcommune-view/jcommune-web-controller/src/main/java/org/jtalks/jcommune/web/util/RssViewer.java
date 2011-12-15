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

package org.jtalks.jcommune.web.util;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import org.jtalks.jcommune.model.entity.Topic;
import org.jtalks.jcommune.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class forms a RSS feed
 *
 * @author Andrey Kluev
 */
public class RssViewer extends AbstractRssFeedView {

    private SecurityService securityService;

    /**
     * Default constructor
     */
    public RssViewer() {
    }

    /**
     * Need for get current user
     *
     * @param securityService security service
     */
    @Autowired
    public RssViewer(SecurityService securityService) {
        this.securityService = securityService;
    }

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

        feed.setTitle("Java forum JTalks ");
        feed.setDescription("Programmers forum");
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

        List<Topic> listContent = (List<Topic>) model.get("topics");
        if (listContent == null) {
            response.sendRedirect(request.getContextPath() + "/errors/404");
            return null;
        }
        List<Item> items = new ArrayList<Item>(listContent.size());

        for (Topic topic : listContent) {

            items.add(createFeedItem(topic, request));
        }

        return items;
    }

    /**
     * @param topic   news topic
     * @param request HttpServletRequest
     * @return item for news feed
     */
    private Item createFeedItem(Topic topic, HttpServletRequest request) {

        Item item = new Item();
        Description description = new Description();
        description.setType("text");
        description.setValue(topic.getLastPost().getShortContent());

        Content content = new Content();
        item.setContent(content);
        Pagination pag = new Pagination(1, securityService.getCurrentUser(), topic.getPosts().size(), true);

        item.setTitle(topic.getTitle());
        item.setAuthor(topic.getLastPost().getUserCreated().getEncodedUsername());

        item.setLink(buildURL(request)
                + "/topics/" + topic.getId()
                + "?page=" + pag.getMaxPages()
                + "#" + topic.getLastPost().getId());

        item.setComments(topic.getTopicStarter().getSignature());
        item.setDescription(description);
        item.setPubDate(topic.getModificationDate().toDate());
        return item;
    }

    /**
     * @param request HttpServletRequest
     * @return url
     */
    public String buildURL(HttpServletRequest request) {
        return request.getScheme()
                + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }

}