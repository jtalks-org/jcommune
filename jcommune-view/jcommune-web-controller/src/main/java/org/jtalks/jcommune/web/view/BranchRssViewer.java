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
import com.sun.syndication.feed.rss.Item;
import org.jtalks.jcommune.model.entity.Branch;
import org.jtalks.jcommune.model.entity.Post;
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

    public static final String DEFAULT_FEED_TITLE = "";
    public static final String DEFAULT_FEED_DESCRIPTION = "";

    /**
     * Sets meta data for the whole RSS feed
     *
     * @param model   RSS model
     * @param feed    RSS feed
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
        } else {
            feed.setTitle(DEFAULT_FEED_TITLE);
            feed.setDescription(DEFAULT_FEED_DESCRIPTION);
            feed.setLink(buildURL(request));
        }

        super.buildFeedMetadata(model, feed, request);
    }

    /**
     * Set list data item news in RSS feed
     *
     * @param model    model containing information about RSS items
     * @param request  http request
     * @param response http response
     * @return list of RSS items
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
     * Creates feed item with information about the post
     *
     * @param post post to add to the feed
     * @param componentUrl base url of the forum component
     * @return item for the RSS feed
     */
    private Item createFeedItem(Post post, String componentUrl) {

        Item item = new Item();

        Content content = new Content();
        content.setType(Content.TEXT);
        content.setValue(post.getPostContent());
        item.setContent(content);

        item.setTitle(post.getTopic().getTitle());
        item.setAuthor(post.getUserCreated().getUsername());

        item.setLink(componentUrl + "/posts/" + post.getId());

        item.setPubDate(post.getCreationDate().toDate());
        return item;
    }

    /**
     * Builds base url for the forum items (branches, posts,...)
     *
     * @param request HttpServletRequest
     * @return base url for the forum items
     */
    private String buildURL(HttpServletRequest request) {
        return request.getScheme()
                + "://" + request.getServerName()
                + ":" + request.getServerPort()
                + request.getContextPath();
    }

}