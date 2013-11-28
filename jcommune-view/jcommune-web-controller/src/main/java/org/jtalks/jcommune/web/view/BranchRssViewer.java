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
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class forms a RSS feed view for a branch.
 *
 * @author Andrei Alikov
 */
public class BranchRssViewer extends AbstractRssFeedView {

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
        Branch branch = (Branch)model.get("branch");
        if (branch != null) {
            feed.setTitle(branch.getName());
            feed.setDescription(branch.getDescription());
            feed.setLink(buildURL(request) + branch.prepareUrlSuffix());
        }

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
        List<Post> posts = (List<Post>) model.get("posts");
        if (posts == null) {
            response.sendRedirect(request.getContextPath() + "/errors/404");
            return null;
        }
        List<Item> items = new ArrayList<Item>(posts.size());

        for (Post post : posts) {
            items.add(createFeedItem(post, url));
        }

        response.setContentType(getContentType());
        return items;
    }

    /**
     * Create news item
     *
     * @param post post to add to the feed
     * @param url building URL
     * @return item for news feed
     */
    private Item createFeedItem(Post post, String url) {

        Item item = new Item();
        Description description = new Description();
        description.setType("text");
        description.setValue(post.getPostContent());

        Content content = new Content();
        item.setContent(content);

        item.setTitle(post.getTopic().getTitle());
        item.setAuthor(post.getUserCreated().getUsername());

        item.setLink(url + "/posts/" + post.getId());

        //item.setComments(topic.getTopicStarter().getSignature());
        item.setDescription(description);
        item.setPubDate(post.getCreationDate().toDate());
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