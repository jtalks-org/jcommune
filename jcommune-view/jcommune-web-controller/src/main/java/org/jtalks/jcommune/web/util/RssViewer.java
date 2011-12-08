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
import com.sun.syndication.feed.rss.Item;
import org.jtalks.jcommune.model.entity.Topic;
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
        feed.setLink("http://deploy.jtalks.org/jcommune");

        super.buildFeedMetadata(model, feed, request);
    }

    /**
     * Set list data item news in RSS feed
     *
     * @param model    news model
     * @param request  http request
     * @param response http response
     * @return list items
     * @throws Exception all exception
     */
    @Override
    protected List<Item> buildFeedItems(Map<String, Object> model,
                                        HttpServletRequest request, HttpServletResponse response)
                                        throws IOException {
        List<Item> items = null;
        try {
            List<Topic> listContent = (List<Topic>) model.get("topics");
            items = new ArrayList<Item>(listContent.size());

            for (Topic topic : listContent) {

                Item item = new Item();

                Content content = new Content();
                item.setContent(content);

                item.setTitle(topic.getTitle());
                item.setAuthor(topic.getTopicStarter().getEncodedUsername());
                item.setLink("http://deploy.jtalks.org/jcommune/topics/" + topic.getId());
                item.setComments(topic.getTopicStarter().getSignature());

                items.add(item);
            }
        } catch (Exception ex) {
                response.sendRedirect("/jcommune/errors/404");
        }
        return items;
    }

}